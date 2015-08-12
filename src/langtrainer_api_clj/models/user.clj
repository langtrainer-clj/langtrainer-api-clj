(ns langtrainer-api-clj.models.user
  (:require [com.stuartsierra.component :as component]
            [clojure.java.jdbc :as jdbc]))

(defrecord User [db]
  component/Lifecycle

  (start [this]
    this)
  (stop [this]
    this))

(defn new-user-model []
  (map->User {}))

(defn all [{{db :datasource} :db}]
  (jdbc/query db ["SELECT * FROM users"] :result-set-fn first))

(defn fetch [{{db :datasource} :db} & token]
  (jdbc/query db ["SELECT * FROM users WHERE token=? LIMIT 1" token] :result-set-fn first))
