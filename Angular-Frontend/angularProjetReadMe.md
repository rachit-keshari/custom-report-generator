## Angular Project Development:

node.js installation: v14.21.3 version (node -v)
npm version 6.14.18 compatible: (npm -v), (npm install npm@6.14.18)
Angular Cli updation:
remove older version: (npm uninstall @angular/cli), (npm uninstall -g @angular/cli)
(npm cache clear --force), (npm install -g @angular/cli@12), (ng version)

run powerShell with administrator access: (Set-ExecutionPolicy RemoteSigned), (Get-ExecutionPolicy)

```
ng serve (to live on the server)
npm install bootstrap (bootstrap added in package.json)
npm install jquery (jquery added in package.json)
```
``` 
open angular.json
added bootstrap css and js paths in json paths architect.build.styles & architect.build.scripts [] lists
"styles": [ "src/styles.css", "./node_modules/bootstrap/dist/css/bootstrap.min.css" ],
"scripts": [ "./node_modules/bootstrap/dist/js/bootstrap.js", "./node_modules/jquery/dist/jquery.js" ]
```





