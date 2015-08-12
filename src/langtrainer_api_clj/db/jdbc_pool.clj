(ns langtrainer-api-clj.db.jdbc-pool
  (:import com.mchange.v2.c3p0.ComboPooledDataSource)
  (:require [com.stuartsierra.component :as component]
            [jdbc.pool.c3p0             :as pool]))

;; component for a generic JDBC database

(defrecord JDBCDatabase [db-spec datasource]
  component/Lifecycle

  (start [this]
    (if datasource ; already started
      this
      (assoc this :datasource (pool/make-datasource-spec (:db-spec this)))))

  (stop [this]
    (if (not datasource) ; already stopped
      this
      (do (.close datasource)
          (assoc this :datasource nil)))))

(defn new-database [db-spec]
  (map->JDBCDatabase {:db-spec db-spec}))
