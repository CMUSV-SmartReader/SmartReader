var thermoreader = thermoreader || {};

thermoreader.db = function($http){

	var
    userId =
      localStorage.hasOwnProperty('userId')? JSON.parse(localStorage['userId']):"",
    categoryFeeds =
      localStorage.hasOwnProperty('categoryFeeds')? JSON.parse(localStorage['categoryFeeds']):[],
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
      $http.get("/categories").success(function(data) {
        console.log(data);
        categoryFeeds = [];
        for(var i=0; i<data.length; ++i){
          categoryFeeds.push({ name : data[i].name, feeds : [], id: data[i].id });
          for(var j=0; j<data[i].userFeedsInfos.length; ++j){
              var key = data[i].userFeedsInfos[j]["feedId"];
              var title = data[i].userFeedsInfos[j]["feedTitle"];
              var userFeedId = data[i].userFeedsInfos[j]["userFeedId"];
              var feed = new thermoreader.model.feed(key, title);
              feed.userFeedId = userFeedId;
              categoryFeeds[i].feeds.push(feed);
              feedArticles[key] = {
                name: userFeedId, articles: []
              };
          } 
        } 
        callback(categoryFeeds);
        //localStorage['categoryFeeds'] = JSON.stringify(categoryFeeds);
      });
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
            d[i].link, Math.floor(Math.random()*5+1), false
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
            d[i].link, Math.floor(Math.random()*5+1), false
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
