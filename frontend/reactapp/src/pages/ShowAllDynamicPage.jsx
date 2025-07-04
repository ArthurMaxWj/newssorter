import { useEffect, useState } from 'react';
import Loading from "../components/Loading"
import ArticlesList from "../components/ArticlesList"
import Error from "../components/Error"



function ShowAllDynamicPage(props) {
  // const isDataLoaded = props.store.dynamicArticles.length > 0
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState("")

  useEffect(() => {
    fetch(`/api/dynamic-all?forcememo=${props.isForced}`)
      .then((res) => res.json())
      .then((data) => {
        if (data.success == "true") {
          props.setStore({
            ...props.store,
            dynamicArticles: data.articles
          })
          setLoading(false)
        } else {
          setError(data.error)
          setLoading(false)
        }
      })
      .catch((err) => {
        setError(`"success": "false", "error": "Featch error: ${err}"`)
        setLoading(false)
      })
      // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

  if (loading) return  <Loading 
        text="AI is processing articles, this might take up to 1 minute depending on AI model used"
      />

  if (error != "") return <Error text={error} setPage={props.setPage} />

  return <ArticlesList articles={props.store.dynamicArticles} setPage={props.setPage} />
  
}

export default ShowAllDynamicPage
