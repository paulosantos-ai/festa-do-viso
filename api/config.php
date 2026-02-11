<?php
// Configuração da Base de Dados - Festa do Viso

// Configurações da Base de Dados
define('DB_HOST', 'localhost');
define('DB_NAME', 'festa_viso');
define('DB_USER', 'festa_viso_user');
define('DB_PASS', 'festa_viso_pass');
define('DB_CHARSET', 'utf8mb4');

// Configurações de Sessão
define('ADMIN_SESSION_NAME', 'festa_viso_admin');
define('SESSION_TIMEOUT', 3600); // 1 hora

// Configurações de Segurança
define('ADMIN_PASSWORD_HASH', '$2y$10$ON.M2AMIpe8QEt2TsN20OeOvj8PfMFh5D0V2UncINNQIv6AEUecjy'); // admin123

// Classe de Conexão à Base de Dados
class Database {
    private static $instance = null;
    private $conn;

    private function __construct() {
        try {
            $dsn = "mysql:host=" . DB_HOST . ";dbname=" . DB_NAME . ";charset=" . DB_CHARSET;
            $options = [
                PDO::ATTR_ERRMODE            => PDO::ERRMODE_EXCEPTION,
                PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
                PDO::ATTR_EMULATE_PREPARES   => false,
            ];

            $this->conn = new PDO($dsn, DB_USER, DB_PASS, $options);
        } catch (PDOException $e) {
            error_log("Database Connection Error: " . $e->getMessage());
            http_response_code(500);
            echo json_encode(['error' => 'Erro de conexão à base de dados']);
            exit;
        }
    }

    public static function getInstance() {
        if (self::$instance === null) {
            self::$instance = new self();
        }
        return self::$instance;
    }

    public function getConnection() {
        return $this->conn;
    }
}

// Função auxiliar para enviar resposta JSON
function sendJsonResponse($data, $statusCode = 200) {
    http_response_code($statusCode);
    header('Content-Type: application/json; charset=utf-8');
    echo json_encode($data, JSON_UNESCAPED_UNICODE);
    exit;
}

// Função auxiliar para validar sessão de admin
function verificarSessaoAdmin() {
    session_name(ADMIN_SESSION_NAME);
    session_start();

    if (!isset($_SESSION['admin_logado']) || $_SESSION['admin_logado'] !== true) {
        sendJsonResponse(['error' => 'Não autenticado'], 401);
    }

    // Verificar timeout da sessão
    if (isset($_SESSION['ultimo_acesso']) && (time() - $_SESSION['ultimo_acesso'] > SESSION_TIMEOUT)) {
        session_unset();
        session_destroy();
        sendJsonResponse(['error' => 'Sessão expirada'], 401);
    }

    $_SESSION['ultimo_acesso'] = time();
}

// Configurações de CORS (se necessário)
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type, Authorization');

// Tratar requisições OPTIONS (preflight)
if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit;
}
?>
