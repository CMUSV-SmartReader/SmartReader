var thermoreader = thermoreader || {};

thermoreader.mainCtrl = function($scope, $routeParams, $http, dbFactory) {

  $scope.pageName = $routeParams.pageName;

  $scope.orderRule =
    localStorage.hasOwnProperty('orderRule')? localStorage['orderRule']:"popular";

  $scope.allFeeds = dbFactory.getAllFeeds(function(allFeeds){
    $scope.allFeeds = allFeeds;
  });

  $scope.selectedFeed = [];

  switch($scope.pageName){
    case "home":
    case "recommendation":
      $scope.selectedFeed = dbFactory.getRecommendations(function(recommendations) {
        $scope.selectedFeed = { name: "Recommendations", articles: recommendations };
      });
      break;
    case "feed":
      console.log($routeParams.feedId);
      $scope.selectedFeed = dbFactory.getFeed($routeParams.feedId, function(feed){
        $scope.selectedFeed = feed;
      });
  }

  $scope.expandArticle = function(article){
    article.expanded = !article.expanded;
    $http.put("/article/"+article.id+"/read").success( function(){
      article.read = true;
    });
    dbFactory.getDuplicates(article.id, function(d){
      if(d.length > 0){ article.duplicates = [new thermoreader.model.article(
        article.id, article.title, article.author, article.date,
        article.feedName, article.summary, article.description,
        article.link, article.popular, article.read
      )].concat(d); }
    });
  };

  $scope.replaceArticle = function(article, dupArticle){
    console.log(article);
    console.log(dupArticle);
    article.title = dupArticle.title;
    article.link = dupArticle.link;
    article.date = dupArticle.date;
    article.author = dupArticle.author;
    article.feedName = dupArticle.feedName;
    article.description = dupArticle.description;
  };

  $scope.setOrderRule = function(rule){
    $scope.orderRule = rule;
    localStorage['orderRule'] = rule;
  };

  $scope.$on('$viewContentLoaded', function(){
    $('#side-container').perfectScrollbar({wheelSpeed: 60});
    $('#content-container').perfectScrollbar({wheelSpeed: 60});
  });

};

thermoreader.manageCtrl = function($scope, dbFactory, $http) {

  $scope.allFeeds = dbFactory.getAllFeeds(function(allFeeds) {
    $scope.allFeeds = allFeeds;
  });

  $scope.deleteUserFeed = function(category, feed) {
    console.log(category);
    $http.delete("/category/" + category.id + "/" + feed.userFeedId).success(function() {
      console.log("successful delete");
      delete feed;
    });
  };

  $scope.addNewFeed = function(category) {
    console.log(category.feedURL);
    $http.post('/category/' + category.id + "/add_feed", {
      data: category.feedURL // http://rss.sina.com.cn/news/allnews/tech.xml
    }).success(function() {
      //category.feeds.psuh
    });
    category.visibleInput = false;
  };

  $scope.showNewInput = function(category) {
    category.visibleInput = true;
  };

};
