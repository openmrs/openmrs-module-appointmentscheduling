/*
 * queryParameters -> handles the query string parameters
 * queryString -> the query string without the fist '?' character
 * re -> the regular expression
 * m -> holds the string matching the regular expression
 */
var queryParameters = {}, queryString = location.search.substring(1),
    re = /([^&=]+)=([^&]*)/g, m;
 
// Creates a map with the query string parameters
while (m = re.exec(queryString)) {
    queryParameters[decodeURIComponent(m[1])] = decodeURIComponent(m[2]);
}