(ns user
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.pprint :refer (pprint)]
            [clojure.repl :refer :all]
            [clojure.test :as test]
            [clojure.tools.namespace.repl :refer [refresh refresh-all]]
            [com.stuartsierra.component :as component]
            [langtrainer-api-clj.system :refer [new-system]]
            [clojure.java.jdbc :as jdbc]
            [java-jdbc.sql :as sql]
            [environ.core :refer [env]]))

(declare system)

(defn start
  "The entry-point for 'lein run-dev'"
  [& args]
  (println "\nCreating your [DEV] server...")

  (alter-var-root #'system (constantly (-> (new-system
                                             {:database-url (env :database-url)
                                              :service-map  {:env  :development
                                                             :port (or (first args) 8080)}})
                                           component/start-system))))

(defn stop []
  (component/stop-system system))

(defn reset []
  (stop)
  (refresh :after 'user/start))
