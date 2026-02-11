<?php
require_once 'config.php';

$db = Database::getInstance()->getConnection();
$method = $_SERVER['REQUEST_METHOD'];

try {
    switch ($method) {
        case 'GET':
            // Listar todas as folhas
            $stmt = $db->prepare("
                SELECT f.*,
                       COUNT(r.id) as numeros_ocupados
                FROM folhas f
                LEFT JOIN registos r ON f.id = r.folha_id
                GROUP BY f.id
                ORDER BY f.data_criacao DESC
            ");
            $stmt->execute();
            $folhas = $stmt->fetchAll();

            sendJsonResponse([
                'success' => true,
                'folhas' => $folhas
            ]);
            break;

        case 'POST':
            // Criar nova folha (requer autenticação)
            verificarSessaoAdmin();

            $data = json_decode(file_get_contents('php://input'), true);

            if (!isset($data['nome']) || empty(trim($data['nome']))) {
                sendJsonResponse(['error' => 'Nome da folha é obrigatório'], 400);
            }

            $stmt = $db->prepare("INSERT INTO folhas (nome, ativa) VALUES (?, 1)");
            $stmt->execute([trim($data['nome'])]);

            $novaFolhaId = $db->lastInsertId();

            sendJsonResponse([
                'success' => true,
                'message' => 'Folha criada com sucesso',
                'folha_id' => $novaFolhaId
            ], 201);
            break;

        case 'DELETE':
            // Eliminar folha (requer autenticação)
            verificarSessaoAdmin();

            $data = json_decode(file_get_contents('php://input'), true);

            if (!isset($data['id'])) {
                sendJsonResponse(['error' => 'ID da folha é obrigatório'], 400);
            }

            // Verificar se não é a última folha
            $stmt = $db->prepare("SELECT COUNT(*) as total FROM folhas");
            $stmt->execute();
            $total = $stmt->fetch()['total'];

            if ($total <= 1) {
                sendJsonResponse(['error' => 'Não pode eliminar a última folha'], 400);
            }

            $stmt = $db->prepare("DELETE FROM folhas WHERE id = ?");
            $stmt->execute([$data['id']]);

            sendJsonResponse([
                'success' => true,
                'message' => 'Folha eliminada com sucesso'
            ]);
            break;

        default:
            sendJsonResponse(['error' => 'Método não permitido'], 405);
    }
} catch (PDOException $e) {
    error_log("Database Error: " . $e->getMessage());
    sendJsonResponse(['error' => 'Erro ao processar pedido'], 500);
}
?>
