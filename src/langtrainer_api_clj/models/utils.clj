(ns langtrainer-api-clj.models.utils
  (:use [korma.core :exclude [belongs-to has-many]]))

(defn- fk [fk]
  "Set the foreign key used for an entity relationship."
   {:fk fk})

(defn- define-relation [rel-type this sub-model rel-name options]
  (assoc-in this
            [:entity :rel rel-name]
            (delay
              (create-relation
                (:entity this)
                (:entity sub-model)
                rel-type
                (fk (:fk options))))))

(defn belongs-to [this sub-model rel-name options]
  (define-relation :belongs-to this sub-model rel-name options))

(defn has-many [this sub-model rel-name options]
  (define-relation :has-many this sub-model rel-name options))

(defn find-by [model params]
  (first (select (:entity model)
                 (where params)
                 (limit 1))))
