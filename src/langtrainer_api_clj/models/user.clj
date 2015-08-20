(ns langtrainer-api-clj.models.user
  (:require [crypto.random :as random]
            [clojure.java.jdbc :as jdbc]
            [java-jdbc.sql :as sql]))

(defn find-by [db params]
  (jdbc/query db
    (sql/select [:id :token] :users (sql/where params)) :result-set-fn first))

(defn find-or-create [db token]
  (if-let [user (find-by db {:token token})]
    user
    (jdbc/insert! db :users {:token token})))

(defn generate-token []
  (random/url-part 32))

(defn new-token [db]
  (loop [token (generate-token)]
    (if (find-by db {:token token})
      (recur (generate-token))
      token)))

(defn fetch [db token]
  (if-let [user (find-by db {:token token})]
    user
    {:token (generate-token)}))
