var smartreader = smartreader || {};

function menuCtrl($scope) {
  $scope.categories = [];
  
  smartreader.addCategory = function(category){
    $scope.categories.push(category);
    $scope.$apply();
  }

  smartreader.putCategories = function(categories){
    $scope.categories = categories;
    $scope.$apply();
  }

}

function contentCtrl($scope) {
  $scope.articles = [];

  smartreader.putArticles = function(articles){
    $scope.articles = articles;
    $scope.$apply();
  }


}