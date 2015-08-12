(ns langtrainer-api-clj.core
  (:gen-class) ; for -main method in uberjar
  (:use [ring.adapter.jetty]
        [ring.middleware.resource])
  (:require [langtrainer-api-clj.system :refer [new-system]]
            [com.stuartsierra.component :as component]
            [environ.core :refer [env]]))

(defn fetch-port
  ([] (fetch-port 8080))
  ([default-port] (or (env :port) default-port 8080)))

(defn -main
  "The entry-point for 'lein run'"
  [& args]
  (println "\nCreating your server...")

  (-> (new-system
        {:database-url (env :database-url)
         :service-map  {:env  :production
                        :port (fetch-port (first args))}})
      component/start))
