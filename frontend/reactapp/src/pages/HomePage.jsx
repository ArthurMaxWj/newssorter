import { useState } from 'react'
import Button from '../components/Button'
import ForceCheckbox from '../components/ForceCheckbox'


function HomePage(props) {

  let isAIDataDownloaded = props.store.dynamicArticles.length > 0

  return <div>
    <Button label="See static (no AI)" action={() => props.setPage("showAllStatic")} />
    <br /> or use AI: <br />
    <ForceCheckbox isForced={props.isForced} setIsForced={props.setIsForced} /> <br />
    <Button label="Render Dynamically with AI" action={() => props.setPage("showAllDynamic")} />
    <br /><br />
    Is AI data already dowloaded? <strong>{isAIDataDownloaded ? "Yes" : "No"}</strong>
  </div>
}

export default HomePage
