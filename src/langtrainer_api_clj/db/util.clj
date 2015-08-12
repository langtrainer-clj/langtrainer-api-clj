(ns langtrainer-api-clj.db.util)

(defn make-db-uri [db-url]
  (java.net.URI. db-url))

(defn fetch-user-and-password [db-uri]
  (if (nil? (.getUserInfo db-uri))
        nil (clojure.string/split (.getUserInfo db-uri) #":")))
