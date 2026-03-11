import sqlite3

conexao = sqlite3.connect("usuarios.db")
curso = conexao.cursor()

cursor.execute("""CREATE TABLE IF NOT EXISTS usuarios (
    id INTEGER PRIMARY KEY AUTOINCREMENT, 
    nome TEXT,
    idade INTEGER
)""")

conexao.commit()

nome = input("Digite seu nome: ")
idade = int(input("Digite sua idade: "))

cursor.execute(
    "insert into usuarios (nome, idade) 
    values
    (nome, idade)"
)

conexao.commit()

