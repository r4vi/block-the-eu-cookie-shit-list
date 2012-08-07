(ns genlist.core
  (:use [clojure.tools.cli :only (cli)]
        [clojure.string :only (split-lines join replace-first)]
        [clj-time.core :only (now)]
        [clj-time.format :only (unparse formatter)])
  (:gen-class :main true))

  (defn md5
    "Generate a md5 checksum for the given string"
  [token]
  (let [hash-bytes
        (doto (java.security.MessageDigest/getInstance "MD5")
          (.reset)
           (.update (.getBytes token)))]
    (.toString
      (new java.math.BigInteger 1 (.digest hash-bytes)) ; Positive and the size of the number
         16))) ; Use base16 i.e. hex

  (defn normalise-string
    "turns \r\n and \r into \n in a string"
    [input]
    (join "\n" (split-lines input)))

 (defn update-filter-timestamp
   [in]
   (let [ts-pattern #"(?i)(?m)^\s*!\s*last\smodified[\s\-:]+([\w\+\/=]+).*\n"
         dt-now (str "! Last Modified: " (unparse (formatter "dd MMM yyyy hh:mm z") (now)) "\n")]
    (replace-first in ts-pattern dt-now))) 

  (defn run
    "Prints md5 of input file"
    [opts]
    (let [input (normalise-string (slurp (:file opts))) 
          cleaned (update-filter-timestamp (replace-first (normalise-string input) 
                              #"(?i)(?m)^\s*!\s*checksum[\s\-:]+([\w\+\/=]+).*\n"
                              ""))
          checksum (md5 cleaned)]
      (println checksum)
      (spit (:file opts) 
            (replace-first cleaned "\n" (str "\n! Checksum: " checksum "\n")))))

  (defn -main
    "I don't do a whole lot."
    [& args]
    (let [[opts args banner]
          (cli args
               ["-h" "--help" "Show help" :flag true :default false]
               ["-f" "--file" "REQUIRED: Path to filter FILE"]
               )]
      (when (:help opts)
        (println banner)
        (System/exit 0))
      (if 
        (:file opts)
        (do
          (println "")
          (run opts))
        ;;else
        (println banner))))
