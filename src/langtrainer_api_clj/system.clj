(ns langtrainer-api-clj.system
  (:require [com.stuartsierra.component :as component]
            [langtrainer-api-clj.routes :refer [new-routes]]
            [langtrainer-api-clj.server :refer [new-server]]
            [langtrainer-api-clj.models.home :refer [new-home-model]]
            [langtrainer-api-clj.handlers.home :refer [new-home-handler]]
            [langtrainer-api-clj.db.postgres :refer [new-database]]))

(defn new-system [config-options]
  (component/system-map
    :db (new-database (:database-url config-options))
    :routes (component/using
              (new-routes)
              {:home :handlers/home})
    :server (component/using
           (new-server (:service-map config-options))
           [:routes])
    :models/home (component/using
                   (new-home-model)
                   [:db])
    :handlers/home (component/using
                     (new-home-handler)
                     {:model :models/home})))
