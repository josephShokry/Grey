import { DemoItem } from '@mui/x-date-pickers/internals/demo';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import { DatePicker } from '@mui/x-date-pickers/DatePicker';
import Box from '@mui/material/Box';
import React, { useState, useEffect } from 'react';
import styles from './page.module.css'

// interface CustomDatePickerProps {
//   onDateChange: (newDate: Date) => void;
// }

export default function CustomDatePicker(props:any) {
  const [cleared, setCleared] = React.useState<boolean>(false);

  const handleSetDate = (newDate: any) => {
    let day = newDate?.date();
    let month = newDate?.month() + 1;
    let year = newDate?.year();
    props.onDateChange({day:day, month:month, year:year});
  };

  const handleClearDate = () => {
    let day = null;
    let month = null;
    let year = null;
    props.onDateChange({day:day, month:month, year:year});
    setCleared(true);
  };

  React.useEffect(() => {
    if (cleared) {
      const timeout = setTimeout(() => {
        setCleared(false);
      }, 1500);

      return () => clearTimeout(timeout);
    }
    return () => {};
  }, [cleared]);

  return (
    <LocalizationProvider dateAdapter={AdapterDayjs}>
      <Box className={styles.customDatePickerContainer}>
        <DemoItem label="DatePicker">
          <DatePicker
            className={styles.customDatePicker}
            onChange={handleSetDate}
            slotProps={{
              field: { clearable: true, onClear: handleClearDate },
            }}
          />
        </DemoItem>
      </Box>
    </LocalizationProvider>
  );
}

