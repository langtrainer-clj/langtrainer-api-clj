(ns langtrainer-api-clj.models.training
  (:use [korma.core])
  (:require [langtrainer-api-clj.protocols :as protocols]
            [langtrainer-api-clj.models.utils :refer [fk]]))

(defrecord Training [entity]
  protocols/Model

  (define-relations [this {{units :entity} :unit
                           {users :entity} :user}]
    (-> this
        (assoc-in [:entity :rel "user"]
                  (delay
                    (create-relation
                      (:entity this)
                      units
                      :belongs-to
                      (fk :unit_id))))
        (assoc-in [:entity :rel "unit"]
                  (delay
                    (create-relation
                      (:entity this)
                      users
                      :belongs-to
                      (fk :user_id)))))))

(defn new-training-model [db]
  (map->Training {:entity (-> (create-entity (name "trainings"))
                              (entity-fields :id))}))
