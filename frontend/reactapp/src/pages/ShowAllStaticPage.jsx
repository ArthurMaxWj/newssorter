import { useEffect, useState } from 'react';
import Loading from "../components/Loading"
import ArticlesList from "../components/ArticlesList"
import Error from "../components/Error"



function ShowAllStaticPage(props) {
  const [loading, setLoading] = useState(props.store.staticArticles.length == 0)
  const [error, setError] = useState("")

  useEffect(() => {
    fetch(`/api/static-all?forcememo=${props.isForced}`)
      .then((res) => res.json())
      .then((data) => {
        if (data.success) {
          props.setStore({
            ...props.store,
            staticArticles: data.articles
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

  return <ArticlesList articles={props.store.staticArticles} setPage={props.setPage} />
}

export default ShowAllStaticPage
