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
          for(var j=0; j<data[i].userFeedsInfos.length; ++j){
            for(var key in data[i].userFeedsInfos[j]){
              allFeeds[i].feeds.push(
                new thermoreader.model.feed(
                  key,
                  data[i].userFeedsInfos[j][key]
                )
              );
            }
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
