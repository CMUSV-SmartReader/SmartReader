var thermoreader = thermoreader || {};

thermoreader.instance = angular.module('thermoReader', [])
  .factory('dbFactory', thermoreader.db)
  .controller('mainCtrl', thermoreader.mainCtrl);


$(document).on('ready', function(){
  thermoreader.ui.init();
});
