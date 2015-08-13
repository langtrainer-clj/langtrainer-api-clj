(ns langtrainer-api-clj.db
  (:require [com.stuartsierra.component :as component]
            [korma.db :refer [create-db postgres default-connection]]))

(defrecord JDBCDatabase [db-spec db]
  component/Lifecycle

  (start [this]
    (if db
      this
      (assoc this :db (-> (create-db db-spec)
                          default-connection))))

  (stop [this]
    this))

(defn- make-db-uri [db-url]
  (java.net.URI. db-url))

(defn- fetch-user-and-password [db-uri]
  (if (nil? (.getUserInfo db-uri))
    nil
    (clojure.string/split (.getUserInfo db-uri) #":")))

(defn- new-db-spec [db-url]
  (let [db-uri (make-db-uri db-url)]
    (let [user-and-password (fetch-user-and-password db-uri)]
      (postgres {:db (subs (.getPath db-uri) 1)
                 :user (get user-and-password 0)
                 :password (get user-and-password 1)
                 :host (.getHost db-uri)
                 :port (.getPort db-uri)}))))

(defn new-database [db-url]
  (map->JDBCDatabase {:db-spec (new-db-spec db-url)}))
