(ns vid4.core
  (:require [ring.adapter.jetty :as ring-jetty]
            [reitit.ring :as ring]
            [muuntaja.core :as m]
            [reitit.ring.middleware.muuntaja :as muuntaja])
  (:gen-class))

(def emprestimos (atom {}))

(defn string-handler [_]
  {:status 200
   :body "Sistema"})

(defn validate-emprestimo [body-params]
  (if (and (contains? body-params :nome)
           (contains? body-params :dataInicio)
           (contains? body-params :parcelas)
           (contains? body-params :taxaJuros)
           (contains? body-params :valor)
           (contains? body-params :saldoDevedor))
    nil
    {:status 400
     :body "nome, dataInicio, parcelas, taxaJuros, valor e saldoDevedor sao obrigatorios."}))

(defn create-emprestimos [{:keys [body-params]}]
  (let [validation-error (validate-emprestimo body-params)]
    (if validation-error
      validation-error
      (let [id (str (java.util.UUID/randomUUID))
            emprestimo (assoc body-params :id id)]
        (swap! emprestimos assoc id emprestimo)
        {:status 200
         :body emprestimo}))))

(defn get-emprestimos [_]
  {:status 200
   :body @emprestimos})

(defn get-emprestimo-by-id [{{:keys [id]} :path-params}]
  {:status 200
   :body (get @emprestimos id)})

(def app
  (ring/ring-handler
   (ring/router
    ["/"
     ["emprestimos/:id" get-emprestimo-by-id]
     ["emprestimos" {:get get-emprestimos
                     :post create-emprestimos}]
     ["" string-handler]]
    {:data {:muuntaja m/instance
            :middleware [muuntaja/format-middleware]}})))

(defn start []
  (ring-jetty/run-jetty app {:port  3000
                             :join? false}))

(defn -main
  [& args]
  (start))
