'use client';
import styles from './page.module.css'
import {Button, TextField} from "@mui/material";
import {Box} from "@mui/system";
import ThemeRegistry from "@/app/ThemeRegistry";
import GoogleButton from "@/app/signup/GoogleButton";


function Page() {
    return (
        <Box className={[styles.container]}>
            <Box className={[styles.login_form]}>
                <ThemeRegistry options={{key: 'mui'}}>
                    <TextField className={[styles.textarea]} label='Username' placeholder='pick a username' required
                               helperText="min 5 characters and max 15" size="small"></TextField>
                    <TextField className={[styles.textarea]} label='Email' type="email" placeholder='Email' required
                               size="small"></TextField>
                    <TextField className={[styles.textarea]} label='Password' type="password"
                               placeholder='pick a password' required
                               size="small" helperText="don't share your password with anyone"></TextField>
                    <Button className={[styles.button]} variant="contained" size="large"> Create Account </Button>
                    <GoogleButton />
                </ThemeRegistry>
            </Box>
            <Box className={[styles.panel]}>
                <h1 className={[styles.paneltext]}>GR<br/>&nbsp;&nbsp;EY</h1>
            </Box>
        </Box>
    )
}

export default Page;