(ns langtrainer-api-clj.db
  (:require [com.stuartsierra.component :as component]
            [jdbc.pool.c3p0 :refer [make-datasource-spec]]))

(defrecord JDBCDatabase [db-spec db]
  component/Lifecycle

  (start [this]
    (if db
      this
      (assoc this :db (make-datasource-spec db-spec))))

  (stop [this]
    (if (not db)
      this
      (do
        (.close db)
        (assoc this :db nil)))))

(defn- make-db-uri [db-url]
  (java.net.URI. db-url))

(defn- fetch-user-and-password [db-uri]
  (if (nil? (.getUserInfo db-uri))
    nil
    (clojure.string/split (.getUserInfo db-uri) #":")))

(defn- new-db-spec [db-url]
  (let [db-uri (make-db-uri db-url)
        user-and-password (fetch-user-and-password db-uri)]
    {:classname "org.postgresql.Driver"
     :subprotocol "postgresql"
     :user (get user-and-password 0)
     :password (get user-and-password 1)
     :initial-pool-size 3
     :min-pool-size 3
     :max-pool-size 15
     :ssl true
     :sslfactory "org.postgresql.ssl.NonValidatingFactory"
     :subname (if (= -1 (.getPort db-uri))
                (format "//%s%s" (.getHost db-uri) (.getPath db-uri))
                (format "//%s:%s%s" (.getHost db-uri) (.getPort db-uri) (.getPath db-uri)))}))

(defn new-database [db-url]
  (map->JDBCDatabase {:db-spec (new-db-spec db-url)}))
