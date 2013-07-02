var thermoreader = thermoreader || {};

thermoreader.db = function($http){

	var
    userId = localStorage.hasOwnProperty('userId')? JSON.parse(localStorage['userId']):"",
    categoryFeeds = [],
    recommendations = [],
    feedArticles = {},

    getRecommendations = function(callback){
      $http.get("/article/all_recommend").success(function(d) {
        console.log(d);
        recommendations = [];
        for(var i=0; i<d.length; ++i){
          recommendations.push( new thermoreader.model.article(
            d[i].id, d[i].title, d[i].author, d[i].publishDate,
            "Recommendations", d[i].desc.slice(0, 48), d[i].desc,
            d[i].link, Math.floor(Math.random()*5+1), false
          ));
        };
        callback(recommendations);
      });
      return recommendations;
    },

    getAllFeeds = function(callback){
      if(categoryFeeds.length == 0){
        $http.get("/categories").success(function(data) {
          console.log(data);
          categoryFeeds = [];
          for(var i=0; i<data.length; ++i){
            categoryFeeds.push({ id: data[i].id, name : data[i].name, feeds : [] });
            for(var j=0; j<data[i].userFeedsInfos.length; ++j){
                var feed = new thermoreader.model.feed(
                  data[i].userFeedsInfos[j]["feedId"],
                  data[i].userFeedsInfos[j]["feedTitle"],
                  data[i].userFeedsInfos[j]["userFeedId"],
                  (new Date()), []
                );
                categoryFeeds[i].feeds.push(feed);
                feedArticles[data[i].userFeedsInfos[j]["feedId"]] = feed;
            }
          }
          callback(categoryFeeds);
        });
      }
      return categoryFeeds;
    },

    getFeed = function(feedId, callback){
      $http.get("/feed/"+feedId).success(function(d){
        console.log(d);
        feedArticles[feedId].articles = []; // clear cache
        for(var i=0; i<d.length; ++i){
          feedArticles[feedId].articles.push( new thermoreader.model.article(
            d[i].id, d[i].title, d[i].author || feedArticles[feedId].name, d[i].publishDate,
            feedArticles[feedId].name, d[i].desc.slice(0, 48), d[i].desc,
            d[i].link, Math.floor(Math.random()*5+1), d[i].isRead
          ));
        };
        callback(feedArticles[feedId]);
      });
      return feedArticles[feedId];
    },

    getDuplicates = function(articleId, callback){
      $http.get("/article/"+articleId+"/dup").success(function(d){
        var duplicates = [];
        for(var i=0; i<d.length; ++i){
          duplicates.push( new thermoreader.model.article(
            d[i].id, d[i].title, d[i].author || d[i].feed.title, d[i].publishDate,
            d[i].feed.title, d[i].desc.slice(0, 48), d[i].desc,
            d[i].link, Math.floor(Math.random()*5+1), d[i].isRead
          ));
        }
        callback(duplicates);
      });
    };

	return {
    getRecommendations : getRecommendations,
    getDuplicates : getDuplicates,
    getAllFeeds : getAllFeeds,
    getFeed : getFeed
  };

};
