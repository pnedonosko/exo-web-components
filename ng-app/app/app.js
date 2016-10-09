'use strict';

// Declare app level module which depends on views, and components
angular.module('myApp', [
  'ngRoute',
  'myApp.view1',
  'myApp.view2',
  'myApp.version',
  'exoExplorer',
  'exoExplorerInline'
]).
config(['$locationProvider', '$routeProvider', '$sceDelegateProvider', function($locationProvider, $routeProvider, $sceDelegateProvider) {
  $locationProvider.hashPrefix('!');

  $routeProvider.
	  when('/documents', {
	    template: '<exo-explorer></exo-explorer>'
	  }).
	  when('/documents/:driveName', {
	    template: '<exo-explorer></exo-explorer>'
	  }).
	  when('/documents-inline/:driveName', {
	    template: '<exo-explorer-inline></exo-explorer-inline>'
	  }).
	  otherwise({redirectTo: '/view1'});
  
  $sceDelegateProvider.resourceUrlWhitelist([
     // Allow same origin resource loads.
     'self',
     // Allow loading from our assets domain.  Notice the difference between * and **.
     'https://peter.exoplatform.com.ua:8443/**'
    ]);
}]);
