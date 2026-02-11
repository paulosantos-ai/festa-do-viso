<?php
require_once 'config.php';

$db = Database::getInstance()->getConnection();
$method = $_SERVER['REQUEST_METHOD'];

try {
    switch ($method) {
        case 'GET':
            // Listar todos os vencedores
            $stmt = $db->prepare("
                SELECT *
                FROM vencedores
                ORDER BY data_sorteio DESC, data_registo DESC
            ");
            $stmt->execute();
            $vencedores = $stmt->fetchAll();

            sendJsonResponse([
                'success' => true,
                'vencedores' => $vencedores
            ]);
            break;

        case 'POST':
            // Registar novo vencedor (requer autenticação)
            verificarSessaoAdmin();

            $data = json_decode(file_get_contents('php://input'), true);

            // Validação
            if (!isset($data['folha_id']) || !isset($data['data_sorteio']) || !isset($data['numero_vencedor'])) {
                sendJsonResponse(['error' => 'Dados incompletos'], 400);
            }

            $folhaId = intval($data['folha_id']);
            $dataSorteio = $data['data_sorteio'];
            $numeroVencedor = intval($data['numero_vencedor']);

            // Validar número
            if ($numeroVencedor < 1 || $numeroVencedor > 49) {
                sendJsonResponse(['error' => 'Número vencedor deve estar entre 1 e 49'], 400);
            }

            // Obter informações da folha
            $stmt = $db->prepare("SELECT nome FROM folhas WHERE id = ?");
            $stmt->execute([$folhaId]);
            $folha = $stmt->fetch();

            if (!$folha) {
                sendJsonResponse(['error' => 'Folha não encontrada'], 404);
            }

            // Obter informações do vencedor (número registado)
            $stmt = $db->prepare("
                SELECT nome, contacto
                FROM registos
                WHERE folha_id = ? AND numero = ?
            ");
            $stmt->execute([$folhaId, $numeroVencedor]);
            $vencedor = $stmt->fetch();

            if (!$vencedor) {
                sendJsonResponse([
                    'success' => false,
                    'message' => 'O número ' . $numeroVencedor . ' não foi vendido nesta folha. Não há vencedor.',
                    'sem_vencedor' => true
                ], 200);
            }

            // Inserir vencedor
            $stmt = $db->prepare("
                INSERT INTO vencedores
                (folha_id, folha_nome, data_sorteio, numero_vencedor, vencedor_nome, vencedor_contacto)
                VALUES (?, ?, ?, ?, ?, ?)
            ");
            $stmt->execute([
                $folhaId,
                $folha['nome'],
                $dataSorteio,
                $numeroVencedor,
                $vencedor['nome'],
                $vencedor['contacto']
            ]);

            sendJsonResponse([
                'success' => true,
                'message' => 'Vencedor registado com sucesso',
                'vencedor' => [
                    'numero' => $numeroVencedor,
                    'nome' => $vencedor['nome'],
                    'contacto' => $vencedor['contacto']
                ]
            ], 201);
            break;

        case 'DELETE':
            // Eliminar vencedor (requer autenticação)
            verificarSessaoAdmin();

            $data = json_decode(file_get_contents('php://input'), true);

            if (!isset($data['id'])) {
                sendJsonResponse(['error' => 'ID do vencedor é obrigatório'], 400);
            }

            $stmt = $db->prepare("DELETE FROM vencedores WHERE id = ?");
            $stmt->execute([$data['id']]);

            sendJsonResponse([
                'success' => true,
                'message' => 'Vencedor eliminado com sucesso'
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
