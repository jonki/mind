(ns mind.handlers
  (:require [re-frame.core :as re-frame]
            [mind.db :as db]))

(defn add-thought [db [_ thought]]
  (if (empty? thought)
    db
    (let [new-id (:thought-id db)
          thoughts (:thoughts db)
          find-thought (seq (filter #(= (:title %) thought) thoughts))]
      (if find-thought
        db
        (assoc db
               :thoughts (conj (:thoughts db)
                               {:title thought
                                :content ""
                                :id new-id})

               :thought-id (inc new-id))))))

(defn remove-thought [db [_ id]]
  (let [is-active (= id (:active-thought db))]
    (assoc db
           :thoughts (remove #(= id (:id %)) (:thoughts db))
           :active-thought (if is-active -1 (:active-thought db)))))


(re-frame/register-handler
 :initialize-db
 (fn  [_ _]
   db/default-db))

(re-frame/register-handler
 :set-active-panel
 (fn [db [_ active-panel]]
   (assoc db :active-panel active-panel)))

(re-frame/register-handler
 :search-query-change
 (fn [db [_ search-query]]
   (assoc db :search-query search-query)))

(re-frame/register-handler
 :remove-thought
 remove-thought)

(re-frame/register-handler
 :add-thought
 add-thought)

(re-frame/register-handler
 :activate-thought
 (fn [db [_ thought-id]]
   (assoc db :active-thought thought-id)))

(re-frame/register-handler
 :deactivate-thought
 (fn [db [_ thought-id]]
   (assoc db :active-thought -1)))

(re-frame/register-handler
 :connect-thought
 (fn [db [_ fst-id scnd-id]]
   (let [connections (:connections db)]
     (assoc db :connections (conj connections
                                  #{fst-id scnd-id})))))
