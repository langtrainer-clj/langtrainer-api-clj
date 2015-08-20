(ns langtrainer-api-clj.models.course
  (:require [clojure.java.jdbc :as jdbc]
            [clojure.string :as str]
            [java-jdbc.sql :as sql]))

(defn- find-first [f coll]
  (first (filter f coll)))

(defn- transform-units [result]
  (defn transform-unit [row]
    (-> row
        (assoc :course_slug (:slug
                              (find-first
                                #(= (:id %) (:course_id row))
                                (:courses result))))))
  (assoc result :units (mapv transform-unit (:units result))))

(defn- transform-courses [result]
  (defn transform-course [row]
    (assoc row :units (filter #(= (:course_id %) (:id row)) (:units result))))
  (mapv transform-course (:courses result)))

(defn for-world [db user-id]
  (defn select-courses []
    (jdbc/query db
      (sql/select [:id :slug] :courses (sql/where {:published true}))))

  (defn select-units [course-ids]
    (jdbc/query db
      (sql/select [:id :slug :course_id] :units (sql/where {:course_id course-ids}))))

  (defn select-trainings [user-id unit-ids]
    (jdbc/query db
      (sql/select "current_step_id AS step_id, unit_id"
                  :trainings
                  (sql/where {:user_id user-id :unit_id unit-ids}))))

  (defn select-steps [step-ids]
    (jdbc/query db
      (sql/select [:id :en_answers :ru_answers :en_question :ru_question :ru_help :en_help]
                  :steps
                  (sql/where {:steps.id step-ids}))))

  (defn select-first-steps [unit-ids]
    (jdbc/query db
      (sql/select "unit_id, MIN(steps.id)"
                  :steps
                  (str "INNER " (sql/join :steps_units {:steps.id :steps_units.step_id}))
                  (cons (str "unit_id NOT IN (" (str/join ", " (repeat (count unit-ids) "?")) ")") unit-ids)
                  "GROUP BY unit_id")))

  (let [courses-rows (select-courses)
        units-rows (select-units (mapv :id courses-rows))
        trainings-rows (select-trainings user-id (mapv :id units-rows))
        steps-rows (select-steps (mapv :step_id trainings-rows))
        first-steps-rows (select-first-steps (mapv :unit_id trainings-rows))]
    (println (mapv :unit_id first-steps-rows))
    (-> {:courses courses-rows
         :units units-rows
         :trainings trainings-rows
         :steps steps-rows}
        transform-units
        transform-courses)))
