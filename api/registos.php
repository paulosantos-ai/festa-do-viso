<?php
require_once 'config.php';

$db = Database::getInstance()->getConnection();
$method = $_SERVER['REQUEST_METHOD'];

try {
    switch ($method) {
        case 'GET':
            // Obter registos de uma folha específica
            if (!isset($_GET['folha_id'])) {
                sendJsonResponse(['error' => 'ID da folha é obrigatório'], 400);
            }

            $folhaId = intval($_GET['folha_id']);

            $stmt = $db->prepare("
                SELECT numero, nome, contacto, data_registo
                FROM registos
                WHERE folha_id = ?
                ORDER BY numero ASC
            ");
            $stmt->execute([$folhaId]);
            $registos = $stmt->fetchAll();

            // Converter para formato de objeto com número como chave
            $registosObj = [];
            foreach ($registos as $registo) {
                $registosObj[$registo['numero']] = [
                    'nome' => $registo['nome'],
                    'contacto' => $registo['contacto'],
                    'dataRegisto' => $registo['data_registo']
                ];
            }

            sendJsonResponse([
                'success' => true,
                'registos' => $registosObj
            ]);
            break;

        case 'POST':
            // Criar novo registo
            $data = json_decode(file_get_contents('php://input'), true);

            // Validação
            if (!isset($data['folha_id']) || !isset($data['numero']) || !isset($data['nome']) || !isset($data['contacto'])) {
                sendJsonResponse(['error' => 'Dados incompletos'], 400);
            }

            $folhaId = intval($data['folha_id']);
            $numero = intval($data['numero']);
            $nome = trim($data['nome']);
            $contacto = trim($data['contacto']);

            // Validar número
            if ($numero < 1 || $numero > 49) {
                sendJsonResponse(['error' => 'Número deve estar entre 1 e 49'], 400);
            }

            // Validar nome
            if (empty($nome)) {
                sendJsonResponse(['error' => 'Nome é obrigatório'], 400);
            }

            // Validar contacto (9 dígitos)
            if (!preg_match('/^[0-9]{9}$/', $contacto)) {
                sendJsonResponse(['error' => 'Contacto deve ter 9 dígitos'], 400);
            }

            // Verificar se a folha existe
            $stmt = $db->prepare("SELECT id FROM folhas WHERE id = ?");
            $stmt->execute([$folhaId]);
            if (!$stmt->fetch()) {
                sendJsonResponse(['error' => 'Folha não encontrada'], 404);
            }

            // Verificar se o número já está ocupado
            $stmt = $db->prepare("SELECT id FROM registos WHERE folha_id = ? AND numero = ?");
            $stmt->execute([$folhaId, $numero]);
            if ($stmt->fetch()) {
                sendJsonResponse(['error' => 'Número já está ocupado'], 409);
            }

            // Inserir registo
            $stmt = $db->prepare("
                INSERT INTO registos (folha_id, numero, nome, contacto)
                VALUES (?, ?, ?, ?)
            ");
            $stmt->execute([$folhaId, $numero, $nome, $contacto]);

            sendJsonResponse([
                'success' => true,
                'message' => 'Número registado com sucesso',
                'registo_id' => $db->lastInsertId()
            ], 201);
            break;

        case 'DELETE':
            // Eliminar registo (requer autenticação)
            verificarSessaoAdmin();

            $data = json_decode(file_get_contents('php://input'), true);

            if (!isset($data['id'])) {
                sendJsonResponse(['error' => 'ID do registo é obrigatório'], 400);
            }

            $stmt = $db->prepare("DELETE FROM registos WHERE id = ?");
            $stmt->execute([$data['id']]);

            sendJsonResponse([
                'success' => true,
                'message' => 'Registo eliminado com sucesso'
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
