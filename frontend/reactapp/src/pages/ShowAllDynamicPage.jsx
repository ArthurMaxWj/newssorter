import { useEffect, useState } from 'react';
import Loading from "../components/Loading"

function ShowAllDynamicPage(props) {

  const [ajaxResult, setAjaxResult] = useState("")
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    fetch('/api/static-all')
    .then((res) => res.text()) // TODO: I don't always return JSON, fix it
      .then((data) => {
        setAjaxResult(data);
        console.log("Success")
        setLoading(false);
      })
      .catch((err) => {
        console.error('Fetch error:', err)
        setAjaxResult('Fetch error:', err)
        setLoading(false)
      });
  }, []);

  if (loading) return <Loading 
    text="AI is processing articles, this might take up to 1 minute depending on AI model used"
  />

  return <div>
    {ajaxResult}
  </div>
}

export default ShowAllDynamicPage
