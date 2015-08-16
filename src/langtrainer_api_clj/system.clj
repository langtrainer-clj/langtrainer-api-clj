(ns langtrainer-api-clj.system
  (:require [com.stuartsierra.component :as component]
            [langtrainer-api-clj.routes :refer [new-routes]]
            [langtrainer-api-clj.server :refer [new-server]]
            [langtrainer-api-clj.db :refer [new-database]]
            [langtrainer-api-clj.data :refer [new-data-layer]]))

(defn new-system [config-options]
  (component/system-map
    :db (new-database (:database-url config-options))
    :data (component/using
            (new-data-layer)
            [:db])
    :routes (component/using
              (new-routes)
              [:data])
    :server (component/using
           (new-server (:service-map config-options))
           [:routes])))
