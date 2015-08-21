(ns langtrainer-api-clj.models.course
  (:require [clojure.java.jdbc :as jdbc]
            [clojure.string :as str]
            [java-jdbc.sql :as sql]))

(defn fetch-world [db user-id]
  (defn select-touched []
    (jdbc/query db
      (sql/select ["courses.id AS course_id" "courses.slug AS course_slug" "units.slug AS unit_slug" "units.id AS unit_id" "steps.id AS step_id" :en_answers :ru_answers :en_question :ru_question :ru_help :en_help]
                  :courses
                  (str "INNER " (sql/join :units {:courses.id :units.course_id}))
                  (str "INNER " (sql/join :trainings {:units.id :trainings.unit_id}))
                  (str "INNER " (sql/join :steps {:steps.id :trainings.current_step_id}))
                  (sql/where {:user_id user-id :courses.published true :units.published true}))))

  (defn select-first-steps [unit-ids]
    (jdbc/query db
      (sql/select "MIN(steps.id) AS id"
                  :steps
                  (str "INNER " (sql/join :steps_units {:steps.id :steps_units.step_id}))
                  (when-not (empty? unit-ids)
                    (cons (str "unit_id NOT IN (" (str/join ", " (repeat (count unit-ids) "?")) ")") unit-ids))
                  "GROUP BY unit_id")))

  (defn select-untouched [unit-ids]
    (jdbc/query db
      (sql/select ["steps.id AS step_id" "units.slug AS unit_slug" "steps_units.unit_id AS unit_id" "courses.slug AS course_slug" "courses.id AS course_id" :en_answers :ru_answers :en_question :ru_question :ru_help :en_help]
                  :steps
                  (sql/join :steps_units {:steps.id :steps_units.step_id})
                  (sql/join :units {:units.id :steps_units.unit_id})
                  (sql/join :courses {:courses.id :units.course_id})
                  (sql/where {:steps.id (mapv :id (select-first-steps unit-ids)) :courses.published true :units.published true}))))

  (defn transform-unit [row]
    {:id (:unit_id row)
     :slug (:unit_slug row)
     :course_slug (:course_slug row)
     :current_step {:id (:step_id row)
                    :unit_id (:unit_id row)
                    :en_answers (:en_answers row)
                    :ru_answers (:ru_answers row)
                    :en_question (:en_question row)
                    :ru_question (:ru_question row)
                    :ru_help (:ru_help row)
                    :en_help (:en_help row)}})

  (defn assoc-course [result row]
    (let [slug (keyword (:course_slug row))
          course (slug result)]
      (if course
        (if (first (filter #(= (:id %) (:unit_id row)) (:units course)))
          result
          (assoc-in result [slug :units] (sort-by :id (conj (:units course) (transform-unit row)))))
        (assoc result
               slug
               {:id (:course_id row)
                :slug slug
                :units [(transform-unit row)]}))))

  (let [touched (select-touched)
        untouched (select-untouched (mapv :unit_id touched))]
    (loop [rows   (concat touched untouched)
           result {}]
      (if (empty? rows)
        (sort-by :id (vals result))
        (recur (rest rows) (assoc-course result (first rows)))))))
