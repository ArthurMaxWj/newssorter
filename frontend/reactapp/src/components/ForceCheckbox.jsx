function ForceCheckbox(props) {
  console.log(props.isForced)

  const handleChange = (event) => {
    props.setIsForced(event.target.checked);
  };

  return (
      <label>
        <input
          type="checkbox"
          checked={props.isForced}
          onChange={handleChange}
          disabled={props.isDynamicLoaded}
        />
        Emulate AI (force using memorized answear)
      </label>
  );
}

export default ForceCheckbox
