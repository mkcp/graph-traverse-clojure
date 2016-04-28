(ns graph-traverse.core)

(defn get-graph []
  {:1 [:2 :3]
   :2 [:4 :7]
   :3 [:4]
   :4 [:2 :3 :5]
   :5 [:1 :2 :6]
   :6 []
   :7 [:1 :8]
   :8 [:4 :9]
   :9 [:4 :10]
   :10 [:4 :11]
   :11 [:4 :12]
   :12 [:4 :13]
   :13 [:4]})

(defn traverse-depth
  "Takes a graph data structure and returns a vector of its verticies."
  [graph start]
  (loop [verticies []
         explored #{start}
         frontier [start]]
    (if (empty? frontier)
      verticies
      (let [current (peek frontier)
            neighbors (graph current)]
        (recur
         (conj verticies current)
         (into explored neighbors)
         (into (pop frontier)
               (remove explored neighbors)))))))

(defn seq-traverse-depth
  "Lazily takes graph and returns vector of its verticies."
  [graph start]
  (let [explored #{start}
        frontier [start]]
    ((fn rec-dfs [explored frontier]
       (lazy-seq
        (if (empty? frontier)
          nil
          (let [v (peek frontier)
                neighbors (graph v)]
            (cons v
                  (rec-dfs
                   (into explored neighbors)
                   (into (pop frontier)
                         (remove explored neighbors))))))))
     explored frontier)))

(defn seq-traverse-breadth
  "Lazily takes graph and returns vector of its verticies."
  [graph start]
  (let [explored #{start}
        frontier (conj (clojure.lang.PersistentQueue/EMPTY) start)]
    ((fn rec-dfs [explored frontier]
       (lazy-seq
        (if (empty? frontier)
          nil
          (let [v (peek frontier)
                neighbors (graph v)]
            (cons v
                  (rec-dfs
                   (into explored neighbors)
                   (into (pop frontier)
                         (remove explored neighbors))))))))
     explored frontier)))

(defn search
  "Lazily steps through a graph returning a vertex each recur."
  [graph explored frontier]
  (lazy-seq
   (if (empty? frontier)
     nil
     (let [v (peek frontier)
           neighbors (graph v)]
       (cons v
             (search
              graph
              (into explored neighbors)
              (into (pop frontier)
                    (remove explored neighbors))))))))

(defn traverse
  "Selects the data types for `search`.
  Defaults are {:start :1
                :direction :depth}."
  [graph {:keys [direction start]}]
  (let [direction (case direction
                    :depth []
                    :breadth (clojure.lang.PersistentQueue/EMPTY)
                    nil [])
        start (or start :1)]
    (search graph #{start} (conj direction start))))
