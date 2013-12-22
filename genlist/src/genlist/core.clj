(ns genlist.core
  (:use [clojure.tools.cli :only (cli)]
        [clojure.string :only (split-lines join replace-first)]
        [clj-time.core :only (now)]
        [clj-time.format :only (unparse formatter)]
        [clj-message-digest.core :only (md5-base64)])
  (:gen-class :main true))

  (defn normalise-string
    "turns \r\n and \r into \n in a string"
    [input]
    (join "\n" (split-lines input)))

 (defn update-filter-timestamp
  "Ugly function to update timestamp of filter list. mostly stolen from the original Python"
   [in]
   (let [ts-pattern #"(?i)(?m)^\s*!\s*last\smodified[\s\-:]+([\w\+\/=]+).*\n"
         dt-now (str "! Last Modified: " (unparse (formatter "dd MMM yyyy hh:mm z") (now)) "\n")]
    (replace-first in ts-pattern dt-now))) 

(def checksum-regex #"(?i)(?m)^\s*!\s*checksum[\s\-:]+([\w\+\/=]+).*\n")

  (defn run
    "Prints md5 of input file"
    [opts]
    (let [input (normalise-string (slurp (:file opts))) 
          cleaned (update-filter-timestamp
                   (replace-first (normalise-string input) checksum-regex ""))
          [header body] (clojure.string/split cleaned #"!\n")
          rules (sort (clojure.string/split body #"\n"))
          sorted-rules (clojure.string/join "\n" rules)
          joined (clojure.string/join "!\n" [header (str sorted-rules "\n")])
          checksum (md5-base64 joined)]
      (println checksum)
      (spit (:file opts) 
            (replace-first joined "\n" (str "\n! Checksum: " checksum "\n")))))

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
