(ns langtrainer-api-clj.db
  (:import com.mchange.v2.c3p0.ComboPooledDataSource)
  (:require [com.stuartsierra.component :as component]))

(defn- as-properties [m]
  (let [p (java.util.Properties.)]
    (doseq [[k v] m]
      (.setProperty p (name k) (str v)))
    p))

(defrecord DataSource [datasource]
  java.io.Closeable
  (close [_]
    (.close datasource)))

(defn new-connection-pool
  "Create a connection pool for the given database spec."
  [{:keys [connection-uri subprotocol subname classname
           excess-timeout idle-timeout
           initial-pool-size minimum-pool-size maximum-pool-size
           test-connection-query
           idle-connection-test-period
           test-connection-on-checkin
           test-connection-on-checkout]
    :or {excess-timeout (* 30 60)
         idle-timeout (* 3 60 60)
         initial-pool-size 3
         minimum-pool-size 3
         maximum-pool-size 15
         test-connection-query nil
         idle-connection-test-period 0
         test-connection-on-checkin false
         test-connection-on-checkout false}
    :as spec}]
  (->DataSource (doto (ComboPooledDataSource.)
    (.setDriverClass classname)
    (.setJdbcUrl (or connection-uri (str "jdbc:" subprotocol ":" subname)))
    (.setProperties (as-properties (dissoc spec
                                           :classname :subprotocol :subname :connection-uri
                                           :naming :delimiters :alias-delimiter
                                           :excess-timeout :idle-timeout
                                           :initial-pool-size :minimum-pool-size :maximum-pool-size
                                           :test-connection-query
                                           :idle-connection-test-period
                                           :test-connection-on-checkin
                                           :test-connection-on-checkout)))
    (.setMaxIdleTimeExcessConnections excess-timeout)
    (.setMaxIdleTime idle-timeout)
    (.setInitialPoolSize initial-pool-size)
    (.setMinPoolSize minimum-pool-size)
    (.setMaxPoolSize maximum-pool-size)
    (.setIdleConnectionTestPeriod idle-connection-test-period)
    (.setTestConnectionOnCheckin test-connection-on-checkin)
    (.setTestConnectionOnCheckout test-connection-on-checkout)
    (.setPreferredTestQuery test-connection-query))))

(defrecord JDBCDatabase [db-spec db]
  component/Lifecycle

  (start [this]
    (if db
      this
      (assoc this :db (new-connection-pool db-spec))))

  (stop [this]
    (if (not db)
      this
      (do
        (.close db)
        (assoc this :db nil)))))

(defn- make-db-uri [db-url]
  (java.net.URI. db-url))

(defn- fetch-user-and-password [db-uri]
  (if (nil? (.getUserInfo db-uri))
    nil
    (clojure.string/split (.getUserInfo db-uri) #":")))

(defn- new-db-spec [db-url]
  (let [db-uri (make-db-uri db-url)
        user-and-password (fetch-user-and-password db-uri)]
    {:classname "org.postgresql.Driver"
     :subprotocol "postgresql"
     :user (get user-and-password 0)
     :password (get user-and-password 1)
     :initial-pool-size 3
     :min-pool-size 3
     :max-pool-size 15
     :ssl true
     :sslfactory "org.postgresql.ssl.NonValidatingFactory"
     :subname (if (= -1 (.getPort db-uri))
                (format "//%s%s" (.getHost db-uri) (.getPath db-uri))
                (format "//%s:%s%s" (.getHost db-uri) (.getPort db-uri) (.getPath db-uri)))}))

(defn new-database [db-url]
  (map->JDBCDatabase {:db-spec (new-db-spec db-url)}))
