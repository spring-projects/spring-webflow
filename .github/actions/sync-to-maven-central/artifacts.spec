{
  "files": [
    {
      "aql": {
        "items.find": {
          "$and": [
            {
              "@build.name": "${buildName}",
              "@build.number": "${buildNumber}",
              "path": {
                "$nmatch": "org/springframework/webflow/webflow/webflow-*.zip"
              }
            }
          ]
        }
      },
      "target": "nexus/"
    }
  ]
}
