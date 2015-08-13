(ns langtrainer-api-clj.db.postgres
  (:require [langtrainer-api-clj.db.jdbc-pool :as jdbc]
            [langtrainer-api-clj.db.util :as util]))

(defn new-database [db-url]
  (let [db-uri (util/make-db-uri db-url)]
    (let [user-and-password (util/fetch-user-and-password db-uri)]
      (jdbc/new-database
        {:classname "org.postgresql.Driver"
         :subprotocol "postgresql"
         :user (get user-and-password 0)
         :password (get user-and-password 1)
         :initial-pool-size 3
         :min-pool-size 3
         :max-pool-size 15
         :subname (if (= -1 (.getPort db-uri))
                    (format "//%s%s" (.getHost db-uri) (.getPath db-uri))
                    (format "//%s:%s%s" (.getHost db-uri) (.getPort db-uri) (.getPath db-uri)))}))))
