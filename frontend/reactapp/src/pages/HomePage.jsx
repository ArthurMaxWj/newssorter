import Button from '../components/Button'
import ForceCheckbox from '../components/ForceCheckbox'
import RefreshButton from '../components/RefreshButton'

function HomePage(props) {

  const isAIDataDownloaded = props.store.dynamicArticles.length > 0

  return <section id="home-page">
    <Button label="See static (no AI)" action={() => props.setPage("showAllStatic")} />
    <br /> or use AI: <br />
    <ForceCheckbox isForced={props.isForced} setIsForced={props.setIsForced} isDynamicLoaded={isAIDataDownloaded} /> <br />
    <Button label="Render Dynamically with AI" action={() => props.setPage("showAllDynamic")} />
    <br /><br />
    Is AI data already downloaded? 
    <strong>
      <span> {isAIDataDownloaded ? 'Yes' : 'No'} </span>
      {isAIDataDownloaded && <RefreshButton />}
    </strong>

  </section>
}

export default HomePage
