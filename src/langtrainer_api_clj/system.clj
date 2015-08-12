(ns langtrainer-api-clj.system
  (:require [com.stuartsierra.component :as component]
            [langtrainer-api-clj.routes :refer [new-routes]]
            [langtrainer-api-clj.server :refer [new-server]]
            [langtrainer-api-clj.models.user :refer [new-user-model]]
            [langtrainer-api-clj.handlers.world :refer [new-world-handler]]
            [langtrainer-api-clj.db.postgres :refer [new-database]]))

(defn new-system [config-options]
  (component/system-map
    :db (new-database (:database-url config-options))
    :routes (component/using
              (new-routes)
              {:world :handlers/world})
    :server (component/using
           (new-server (:service-map config-options))
           [:routes])
    :models/user (component/using
                   (new-user-model)
                   [:db])
    :handlers/world (component/using
                     (new-world-handler)
                     {:user :models/user})))
