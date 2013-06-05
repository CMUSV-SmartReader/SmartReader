var smartreader = smartreader || {};

function menuCtrl($scope, $http) {
  $scope.categories = [];

  smartreader.addCategory = function(category){
    $scope.categories = $http.get("/categories");
    $scope.$apply();
  }

  smartreader.putCategories = function(categories){
    $scope.categories = categories;
    $scope.$apply();
  }

  smartreader.loadCategories = function(){
    var userCategories = [];
    $http.get("/categories").success(function(data) {
      console.log(data);
      for(var i=0; i<data.length; ++i){
        userCategories.push({
          name : data.name
        });
      }
    });

    smartreader.putCategories(userCategories);
  }

}

function contentCtrl($scope) {
  $scope.title = "";
  $scope.articles = [];

  smartreader.putTitle = function(title){
    $scope.title = title;
    $scope.$apply();
  }

  smartreader.putArticles = function(articles){
    $scope.articles = articles;
    $scope.$apply();
  }


}
