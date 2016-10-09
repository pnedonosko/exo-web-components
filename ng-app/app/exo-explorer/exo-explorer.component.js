/**
 * Register the `exoExplorer` component on the `exoExplorer` module,
 */
angular.module('exoExplorer').component('exoExplorer', {
  templateUrl : 'exo-explorer/exo-explorer.template.html',
  controller : [ '$routeParams', function ExoExplorerController($routeParams) {
  	//.spaces.testa
  	var driveName = $routeParams.driveName ? $routeParams.driveName : '';
	  this.explorerUrl = 'https://peter.exoplatform.com.ua:8443/portal/_component/documents?driveName=' + driveName;
  } ]
});