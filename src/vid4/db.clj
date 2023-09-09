(ns vid4.db
  (:require [clojure.java.jdbc :as jdbc]))

(def db-config
  {:classname "org.sqlite.JDBC"
   :subprotocol "sqlite"
   :subname "emprestimos.db"}) ;; Substitua "seu_banco_de_dados.db" pelo nome do arquivo SQLite que deseja usar

(defn create-table-if-not-exists []
  (jdbc/db-do-commands db-config
                       (jdbc/create-table-ddl :emprestimos
                                              [:id "INTEGER PRIMARY KEY"
                                               :nome "TEXT"
                                               :dataInicio "TEXT"
                                               :parcelas "INTEGER"
                                               :taxaJuros "REAL"
                                               :valor "REAL"
                                               :saldoDevedor "REAL"])))

(defn insert-emprestimo [emprestimo]
  (jdbc/insert! db-config
                :emprestimos
                {:nome (:nome emprestimo)
                 :dataInicio (:dataInicio emprestimo)
                 :parcelas (:parcelas emprestimo)
                 :taxaJuros (:taxaJuros emprestimo)
                 :valor (:valor emprestimo)
                 :saldoDevedor (:saldoDevedor emprestimo)}))

(defn get-emprestimos-from-db []
  (jdbc/query db-config
              ["SELECT * FROM emprestimos"]
              {:row-fn keyword}))

(defn get-emprestimo-by-id-from-db [id]
  (jdbc/query db-config
              ["SELECT * FROM emprestimos WHERE id = ?" id]
              {:row-fn keyword}))
