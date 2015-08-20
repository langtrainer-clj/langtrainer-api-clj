(ns langtrainer-api-clj.system
  (:require [com.stuartsierra.component :as component]
            [langtrainer-api-clj.routes :refer [new-routes]]
            [langtrainer-api-clj.server :refer [new-server]]
            [langtrainer-api-clj.db :refer [new-database]]))

(defn new-system [config-options]
  (component/system-map
    :db (new-database (:database-url config-options))
    :routes (component/using
              (new-routes)
              [:db])
    :server (component/using
           (new-server (:service-map config-options))
           [:routes])))
