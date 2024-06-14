<?php

include_once dirname(__FILE__) . '/DbConnect.php';

class DbOperation {
    private $con;

    function __construct() {
        $db = new DbConnect();
        $this->con = $db->connect();
    }

    function createProblema($foto, $descricao) {
        $stmt = $this->con->prepare("INSERT INTO problemas (foto, descricao) VALUES (?, ?)");
        $stmt->bind_param("ss", $foto, $descricao);
        if ($stmt->execute()) {
            return true;
        } else {
            return false;
        }
    }

	function getProblemas(){
		$stmt = $this->con->prepare("SELECT id, foto, descricao FROM problemas");
		$stmt->execute();
		$stmt->bind_result($id, $foto, $descricao);
		
		$users = array(); 
		
		while($stmt->fetch()){
			$user  = array();
			$user['id'] = $id; 
			$user['foto'] = $foto; 
			$user['descricao'] = $descricao;  
			
			array_push($users, $user); 
		}
		return $users; 
	}

	function createuser($nome, $email, $senha){
		$stmt = $this->con->prepare("INSERT INTO users (nome, email, senha) VALUES (?, ?, ?)");
		$stmt->bind_param("sss", $nome, $email, $senha);
		if($stmt->execute())
			return true; 
		return false; 
	}
	
	function getUser(){
		$stmt = $this->con->prepare("SELECT id, nome, email, senha FROM users");
		$stmt->execute();
		$stmt->bind_result($id, $nome, $email, $senha);
		
		$users = array(); 
		
		while($stmt->fetch()){
			$user  = array();
			$user['id'] = $id; 
			$user['nome'] = $nome; 
			$user['email'] = $email; 
			$user['senha'] = $senha; 
			
			array_push($users, $user); 
		}
		return $users; 
	}
	
	
	function updateuser($id, $nome, $email, $senha){
		$stmt = $this->con->prepare("UPDATE users SET nome = ?, email = ?, senha = ?, WHERE id = ?");
		$stmt->bind_param("sssi", $nome, $email, $senha, $id);
		if($stmt->execute())
			return true; 
		return false; 
	}
	
	function deleteuser($id){
		$stmt = $this->con->prepare("DELETE FROM users WHERE id = ? ");
		$stmt->bind_param("i", $id);
		if($stmt->execute())
			return true; 
		
		return false; 
	}
}

$response = array();