import { useEffect, useState } from 'react';
import Loading from "../components/Loading"
import ArticlesList from "../components/ArticlesList"
import Error from "../components/Error"



function ShowAllStaticPage(props) {
  const isDataLoaded = props.store.staticArticles.length > 0
  if (isDataLoaded) return <ArticlesList articles={props.store.staticArticles} setPage={props.setPage} />

  const [loading, setLoading] = useState(!isDataLoaded)
  const [error, setError] = useState("")


  useEffect(() => {
    fetch('/api/static-all')
      .then((res) => res.json())
      .then((data) => {
        if (data.success = "true") {
          props.setStore({
            ...props.store,
            staticArticles: data.articles
          })
        } else {
          setError(data.error)
          setLoading(false)
        }
      })
      .catch((err) => {
        setError(`"success": "false", "error": "Featch error: ${err}"`)
        setLoading(false)
      });
  }, []);

  if (loading) {
      return  <Loading 
        text="AI is processing articles, this might take up to 1 minute depending on AI model used"
      />
  } else {
    // ArticleList technically won't render anyway because it will be caught at top of component
    // I'm leaving it to not have missing case (if somthing unexpected happens)
    if (error != "") return <Error text={error} setPage={props.setPage} />
    else return <ArticlesList articles={props.store.staticArticles} setPage={props.setPage} />
  }
}

export default ShowAllStaticPage
