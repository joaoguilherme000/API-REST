<?php 

	require_once '../includes/DbOperation.php';
	require_once '../includes/DbConnect.php';

	$db = new DbConnect();
	$conn = $db->connect();

	header('Content-Type: application/json');

	function isTheseParametersAvailable($params){
		foreach($params as $param){
			if(!isset($_POST[$param])){
				return false; 
			}
		}
		return true; 
	}
	
	
	$response = array();
	
	if  (isset($_GET['apicall'])) {
		switch($_GET['apicall']) {
			case 'createuser':
				if (isTheseParametersAvailable(array('nome', 'email', 'senha'))) {
					$nome = $_POST['nome'];
					$email = $_POST['email'];
					$fotoPerfil = "";
					$senha = password_hash($_POST['senha'], PASSWORD_DEFAULT);
	
					require_once '../includes/DbConnect.php';
					$db = new DbConnect();
					$conn = $db->connect();
	
					$stmt = $conn->prepare("INSERT INTO users (nome, email, senha, fotoPerfil) VALUES (?, ?, ?, ?)");
					$stmt->bind_param("ssss", $nome, $email, $senha, $fotoPerfil);
	
					if ($stmt->execute()) {
						$response = array('error' => false, 'message' => 'Usuário registrado com sucesso');
					} else {
						$response = array('error' => true, 'message' => 'Erro ao registrar usuário');
					}
	
					$stmt->close();
					$conn->close();
				} else {
					$response = array('error' => true, 'message' => 'Parâmetros faltando');
				}
				break;
			
		
				case 'login':
					if (isTheseParametersAvailable(array('email', 'senha'))) {
						$email = $_POST['email'];
						$senha = $_POST['senha'];
		
						require_once '../includes/DbConnect.php';
						$db = new DbConnect();
						$conn = $db->connect();
						$conn->select_db(DB_NAME);

						// Adicione um log para verificar se os parâmetros estão corretos
						error_log("login tentativa com: $email, senha: $senha");
		
						$stmt = $conn->prepare("SELECT id, nome, email, senha FROM users WHERE email = ?");
						$stmt->bind_param("s", $email);
						$stmt->execute();
						$stmt->store_result();

						if ($stmt->num_rows > 0) {
							// Usuário encontrado, verificar a senha
							$stmt->bind_result($id, $nome, $email, $hashed_password);
							$stmt->fetch();

							// Verifica se a senha fornecida corresponde ao hash no banco de dados
							if (password_verify($senha, $hashed_password)) {
								// Senha correta, login bem-sucedido
								$response = array(
									'error' => false,
									'message' => 'Login realizado com sucesso',
									'user' => array(
										'id' => $id,
										'nome' => $nome,
										'email' => $email
									)
								);
							} else {
								// Senha incorreta
								$response = array('error' => true, 'message' => 'Senha incorreta');
							}
						} else {
							// Usuário não encontrado
							$response = array('error' => true, 'message' => 'Usuário não encontrado');
						}

						$stmt->close();
						$conn->close();
					} else {
						$response = array('error' => true, 'message' => 'Parâmetros faltando');
					}
					break;
		
			case 'updateuser':
				if (isTheseParametersAvailable(array('id', 'nome'))) {
					$id = $_POST['id'];
					$nome = $_POST['nome'];
			
					require_once '../includes/DbConnect.php';
					$db = new DbConnect();
					$conn = $db->connect();
			
					$stmt = $conn->prepare("UPDATE users SET nome = ? WHERE id = ?");
					$stmt->bind_param("si", $nome, $id);
			
					if ($stmt->execute()) {
						$response['error'] = false;
						$response['message'] = 'Nome de usuário atualizado com sucesso';
					} else {
						$response['error'] = true;
						$response['message'] = 'Erro ao atualizar nome de usuário';
					}
			
					$stmt->close();
					$conn->close();
				} else {
					$response['error'] = true;
					$response['message'] = 'Parâmetros faltando';
				}
			break;
			
			
			case 'deleteuser':

				if(isset($_GET['id'])){
					$db = new DbOperation();
					if($db->deleteUser($_GET['id'])){
						$response['error'] = false; 
						$response['message'] = 'excluído com sucesso';
						$response['users'] = $db->getUsers();
					}else{
						$response['error'] = true; 
						$response['message'] = 'Algum erro ocorreu por favor tente novamente';
					}
				}else{
					$response['error'] = true; 
					$response['message'] = 'Não foi possível deletar, forneça um id por favor';
				}
			break;

			case 'doLogin': // isso aqui foi criado porque a tela de login não funciona mas isso funciona
				require_once '../includes/DbConnect.php';
				$db = new DbConnect();
				$conn = $db->connect();
				$conn->select_db(DB_NAME);

				// Dados do usuário específico
				$email = 'admin@admin';
				$senha = 'admin';

				// Consulta SQL para encontrar o usuário com o email e senha especificados
				$stmt = $conn->prepare("SELECT id, nome FROM users WHERE email = ? AND senha = ?");
				$stmt->bind_param("ss", $email, $senha);
				$stmt->execute();
				$stmt->store_result();

				if ($stmt->num_rows > 0) {
					// Usuário encontrado
					$stmt->bind_result($id, $nome);
					$stmt->fetch();

					// Exibir mensagem de login bem-sucedido
					echo "Usuário $nome entrou com sucesso.";

				} else {
					// Usuário não encontrado ou credenciais incorretas
					echo "Erro: Usuário não encontrado ou credenciais incorretas.";
				}

				$stmt->close();
				$conn->close();
			break;

			case 'createproblema':
				if (isset($_POST['foto']) && isset($_POST['descricao'])) {
					$foto = $_POST['foto'];
					$descricao = $_POST['descricao'];
			
					require_once '../includes/DbConnect.php';
					$db = new DbConnect();
					$conn = $db->connect();
			
					$stmt = $conn->prepare("INSERT INTO problemas (foto, descricao) VALUES (?, ?)");
					$stmt->bind_param("ss", $foto, $descricao);
			
					if ($stmt->execute()) {
						$response = array('error' => false, 'message' => 'Problema adicionado', 'foto_url' => $foto);
					} else {
						$response = array('error' => true, 'message' => 'Erro ao registrar problema');
					}
			
					$stmt->close();
					$conn->close();
				} else {
					$response = array('error' => true, 'message' => 'Parâmetros faltando');
				}
				break;

				case 'getProblemas':
					if ($_SERVER['REQUEST_METHOD'] === 'GET') {
						require_once '../includes/DbConnect.php';
						$db = new DbConnect();
						$conn = $db->connect();
					
						$stmt = $conn->prepare("SELECT id, foto, descricao FROM problemas");
						$stmt->execute();
						$stmt->bind_result($id, $foto, $descricao);
					
						$problemas = array();
					
						while ($stmt->fetch()) {
							$problema = array();
							$problema['id'] = $id;
							$problema['foto'] = $foto;
							$problema['descricao'] = $descricao;
							$problemas[] = $problema;
						}
				
						// Log para verificar o resultado antes de enviar
						error_log("Lista de problemas obtida: " . json_encode($problemas));
					
						$response = array('error' => false, 'problemas' => $problemas);
					} else {
						$response = array('error' => true, 'message' => 'Método não permitido');
					}
					break;

					case 'getResolvido':
						if ($_SERVER['REQUEST_METHOD'] === 'GET') {
							require_once '../includes/DbConnect.php';
							$db = new DbConnect();
							$conn = $db->connect();
						
							$stmt = $conn->prepare("SELECT id, problema_id, fotoResolvida, descricaoResolvida FROM resolvidos");
							$stmt->execute();
							$stmt->bind_result($id,$problema_id, $fotoResolvida, $descricaoResolvida);
						
							$problemas = array();
						
							while ($stmt->fetch()) {
								$resolvido = array();
								$resolvido['id'] = $id;
								$resolvido['problema_id'] = $id;
								$resolvido['fotoResolvida'] = $fotoResolvida;
								$resolvido['descricaoResolvida'] = $descricaoResolvida;
								$resolvidos[] = $resolvido;
							}
					
							// Log para verificar o resultado antes de enviar
							error_log("Lista de resolvido obtida: " . json_encode($resolvidos));
						
							$response = array('error' => false, 'resolvidos' => $resolvidos);
						} else {
							$response = array('error' => true, 'message' => 'Método não permitido');
						}
						break;

				case 'verImagens':
					if (isset($_GET['id'])) {
						$id = $_GET['id'];
					
						// Recupera a URL da imagem do banco de dados
						require_once '../includes/DbConnect.php';
						$db = new DbConnect();
						$conn = $db->connect();
						$conn->select_db(DB_NAME);
					
						$stmt = $conn->prepare("SELECT foto FROM problemas WHERE id = ?");
						$stmt->bind_param("s", $id);
						$stmt->execute();
						$stmt->bind_result($foto);
					
						if ($stmt->fetch()) {
							// Envie o cabeçalho de tipo de conteúdo da imagem
							header('Content-Type: image/jpeg'); // Ajuste o tipo de conteúdo conforme necessário
					
							// Envie a imagem para o navegador
							readfile($foto); // Supondo que $foto contenha o caminho completo da imagem no servidor
						} else {
							// Caso não encontre a imagem, envie um código de status 404 (Not Found)
							http_response_code(404);
							echo "Imagem não encontrada";
						}
					
						$stmt->close();
						$conn->close();
					} else {
						// Se o parâmetro ID não foi enviado, envie um código de status 400 (Bad Request)
						http_response_code(400);
						echo "Parâmetro ID não fornecido";
					}

				break;

			default:
				$response = array('error' => true, 'message' => 'Chamada de API inválida');
				break;

				
		}
		
	}else{
		 
		$response['error'] = true; 
		$response['message'] = 'Chamada de API inválida';
	}
	

echo json_encode($response);
?>