import { useState } from 'react';

function ForceCheckbox(props) {

  const handleChange = (event) => {
    props.setIsForced(event.target.checked);
  };

  return (
      <label>
        <input
          type="checkbox"
          checked={props.isForced}
          onChange={handleChange}
        />
        Emulate AI (force using memorized answear)
      </label>
  );
}

export default ForceCheckbox
