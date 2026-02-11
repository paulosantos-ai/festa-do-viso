<?php
require_once 'config.php';

$method = $_SERVER['REQUEST_METHOD'];

try {
    switch ($method) {
        case 'POST':
            $data = json_decode(file_get_contents('php://input'), true);
            $action = isset($data['action']) ? $data['action'] : '';

            if ($action === 'login') {
                // Login
                if (!isset($data['password'])) {
                    sendJsonResponse(['error' => 'Password é obrigatória'], 400);
                }

                $password = $data['password'];

                // Verificar password (usando hash fixo por simplicidade)
                // Em produção, deveria verificar contra a base de dados
                if (password_verify($password, ADMIN_PASSWORD_HASH)) {
                    session_name(ADMIN_SESSION_NAME);
                    session_start();

                    $_SESSION['admin_logado'] = true;
                    $_SESSION['ultimo_acesso'] = time();

                    sendJsonResponse([
                        'success' => true,
                        'message' => 'Login realizado com sucesso'
                    ]);
                } else {
                    sendJsonResponse([
                        'success' => false,
                        'error' => 'Password incorreta'
                    ], 401);
                }

            } elseif ($action === 'logout') {
                // Logout
                session_name(ADMIN_SESSION_NAME);
                session_start();
                session_unset();
                session_destroy();

                sendJsonResponse([
                    'success' => true,
                    'message' => 'Logout realizado com sucesso'
                ]);

            } elseif ($action === 'check') {
                // Verificar se está logado
                session_name(ADMIN_SESSION_NAME);
                session_start();

                $logado = isset($_SESSION['admin_logado']) && $_SESSION['admin_logado'] === true;

                if ($logado) {
                    // Verificar timeout
                    if (isset($_SESSION['ultimo_acesso']) && (time() - $_SESSION['ultimo_acesso'] > SESSION_TIMEOUT)) {
                        session_unset();
                        session_destroy();
                        $logado = false;
                    } else {
                        $_SESSION['ultimo_acesso'] = time();
                    }
                }

                sendJsonResponse([
                    'logado' => $logado
                ]);

            } else {
                sendJsonResponse(['error' => 'Ação inválida'], 400);
            }
            break;

        default:
            sendJsonResponse(['error' => 'Método não permitido'], 405);
    }
} catch (Exception $e) {
    error_log("Auth Error: " . $e->getMessage());
    sendJsonResponse(['error' => 'Erro ao processar autenticação'], 500);
}
?>
