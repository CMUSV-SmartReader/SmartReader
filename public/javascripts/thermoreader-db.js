var thermoreader = thermoreader || {};

thermoreader.db = function($http){

	var
    userId = "",
    allFeeds = [],
    articles = {},

    getAllFeeds = function(){
      $http.get("/categories").success(function(data) {
        console.log(data);
        for(var i=0; i<data.length; ++i){
          allFeeds.push({
            name : data[i].name,
            feeds : []
          });
          for(var j=0; j<data[i].userFeeds.length; ++j){
            allFeeds[i].feeds.push(
              new thermoreader.model.feed(data[i].userFeeds[j].id, data[i].userFeeds[j].name)
            );
          }
        }
      });
    },

    getFeed = function(feedId){
      return $http.get("/feed/"+feedId);
    };

    getAllFeeds();

	return {
    allFeeds : allFeeds,
    getFeed : getFeed
  };

};
