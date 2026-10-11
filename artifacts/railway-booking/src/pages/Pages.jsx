import { useMemo, useState } from 'react';
import { Link, useLocation, useParams } from 'wouter';
import { useQueryClient } from '@tanstack/react-query';
import {
  useGetRailwayProviderStatus, useSearchTrains, getSearchTrainsQueryKey,
  useGetTrainRuntimeStatus, getGetTrainRuntimeStatusQueryKey,
  useGetSession, getGetSessionQueryKey, useSignUp, useLogIn, useLogOut,
  useGetMyProfile, getGetMyProfileQueryKey, useUpdateMyProfile,
  useGetMyBookingSummary, getGetMyBookingSummaryQueryKey, useListMyBookings,
  getListMyBookingsQueryKey, useCreateBooking, usePayForBooking,
  useGetTicketByPnr, getGetTicketByPnrQueryKey,
} from '@workspace/api-client-react';
import {
  ArrowRight, ArrowUpDown, Check, ChevronRight, CircleHelp,
  Download, FileSearch, MapPin, Menu, Search, ShieldCheck,
  TrainFront, UserRound, WifiOff, X,
} from 'lucide-react';
import TrainHeroLoader from '../components/TrainHeroLoader.jsx';

const today = () => { const d=new Date(); return `${d.getFullYear()}-${String(d.getMonth()+1).padStart(2,'0')}-${String(d.getDate()).padStart(2,'0')}`; };
const niceDate = (value) => value ? new Date(`${value}T12:00:00`).toLocaleDateString('en-IN', { day:'numeric', month:'short', year:'numeric' }) : '—';
const inr = (value) => `₹${Number(value || 0).toLocaleString('en-IN')}`;
const errText = (error) => error?.message || error?.response?.data?.message || 'We could not complete that request. Please try again.';
const stationLabel = (value) => value ? value.split(/[\[(]/)[0].trim() : '';

export function Shell({ children }) {
  const [path] = useLocation();
  const [menuOpen, setMenuOpen] = useState(false);
  const items = [['/', 'Plan journey'], ['/runtime', 'Train status'], ['/ticket', 'Retrieve ticket'], ['/account', 'My bookings']];
  return <div className="app-shell">
    <header className="topbar">
      <Link href="/" className="brand" aria-label="Railway home"><span className="brand-mark"><TrainFront size={21}/></span><span>raily<em>.</em></span></Link>
      <nav className="nav-links">{items.map(([href,label]) => <Link key={href} href={href} className={path === href ? 'active' : ''}>{label}</Link>)}</nav>
      <div className="nav-right"><Link className="nav-cta" href="/sign-in">Sign in</Link><Link className="nav-cta filled" href="/ticket">Find my ticket <ArrowRight size={13}/></Link><button className="mobile-menu" onClick={() => setMenuOpen(!menuOpen)} aria-label="Open navigation" data-testid="button-mobile-menu">{menuOpen ? <X/> : <Menu/>}</button></div>
    </header>
    {menuOpen && <nav className="mobile-menu-panel">{items.map(([href,label]) => <Link key={href} href={href} onClick={() => setMenuOpen(false)}>{label}<ChevronRight size={16}/></Link>)}</nav>}
    {children}
    <nav className="mobile-bottom-nav">{items.map(([href,label], index) => { const Icon = [TrainFront,MapPin,FileSearch,UserRound][index]; return <Link key={href} href={href} className={path === href ? 'active' : ''}><Icon/><span>{['Plan','Status','Ticket','Account'][index]}</span></Link>; })}</nav>
  </div>;
}

function ProviderBadge() {
  const provider = useGetRailwayProviderStatus();
  const connected = provider.data?.connected;
  return <span className="provider-pill" data-testid="status-provider"><i className="provider-dot" style={connected ? {background:'#4eaa72',boxShadow:'0 0 0 4px #4eaa721c'} : {}}/>{provider.isLoading ? 'Checking live rail connection' : connected ? 'Live railway data connected' : 'Live railway data not connected'}</span>;
}

export function HomePage() {
  const [, setLocation] = useLocation();
  const [form, setForm] = useState({ fromStation:'', toStation:'', journeyDate:today() });
  const [error, setError] = useState('');
  const update = (key) => (event) => setForm({...form,[key]:event.target.value});
  const submit = (event) => {
    event.preventDefault(); setError('');
    if (form.fromStation.trim().length < 2 || form.toStation.trim().length < 2) return setError('Enter both station names to search.');
    if (form.fromStation.trim().toLowerCase() === form.toStation.trim().toLowerCase()) return setError('Choose two different stations.');
    const params = new URLSearchParams(form); setLocation(`/search?${params.toString()}`);
  };
  const swap = () => setForm({...form,fromStation:form.toStation,toStation:form.fromStation});
  return <>
    <main>
      <section className="home-hero">
        <div className="hero-grid"/>
        <div className="hero-copy"><span className="eyebrow">Your journey, in good hands</span><h1>India moves.<br/><span>We’ll get you</span><br/>there.</h1><p>Plan your rail journey with clarity, keep every detail close, and travel with a little more peace of mind.</p></div>
        <TrainHeroLoader />
        <div className="search-wrap"><form className="search-card" onSubmit={submit}>
          <div className="field"><label htmlFor="fromStation">From</label><input id="fromStation" list="station-suggestions" placeholder="Station or city" value={form.fromStation} onChange={update('fromStation')} data-testid="input-from-station" required minLength="2"/></div>
          <button type="button" className="swap-btn" onClick={swap} aria-label="Swap stations" data-testid="button-swap-stations"><ArrowUpDown size={14}/></button>
          <div className="field"><label htmlFor="toStation">To</label><input id="toStation" list="station-suggestions" placeholder="Where to?" value={form.toStation} onChange={update('toStation')} data-testid="input-to-station" required minLength="2"/></div>
          <datalist id="station-suggestions"><option value="New Delhi"/><option value="Mumbai Central"/><option value="Bengaluru City"/><option value="Chennai Central"/><option value="Howrah Junction"/><option value="Pune Junction"/><option value="Hyderabad Deccan"/></datalist>
          <div className="field"><label htmlFor="journeyDate">Journey date</label><input id="journeyDate" type="date" min={today()} value={form.journeyDate} onChange={update('journeyDate')} data-testid="input-journey-date" required/></div>
          <div className="field"><label htmlFor="travellers">Travellers</label><select id="travellers" defaultValue="1" data-testid="select-travellers"><option value="1">1 traveller</option><option value="2">2 travellers</option><option value="3">3 travellers</option><option value="4">4 travellers</option><option value="5">5 travellers</option><option value="6">6 travellers</option></select></div>
          <button className="search-submit" type="submit" data-testid="button-search-trains">Find trains <ArrowRight size={15}/></button>
        </form>{error && <p className="error-text" role="alert">{error}</p>}
        <div className="hero-bottom"><span><ShieldCheck size={14}/> Your details stay yours</span><span><FileSearch size={14}/> Retrieve tickets without an account</span></div></div>
      </section>
      <section className="section">
        <div className="section-kicker">One companion, every leg</div><h2 className="section-heading">The journey is easier<br/>when the details are clear.</h2><p className="section-sub">From the first station search to the ticket in your pocket, keep the important things moving together.</p>
        <div className="quick-grid" style={{marginTop:32}}>
          <article className="quick-card"><TrainFront className="card-graphic" size={100} strokeWidth={1}/><div><span className="section-kicker">A better start</span><h3>Find your way by rail.</h3><p>Search the route and travel date. When live railway data is connected, verified train options will appear here.</p></div><Link className="mini-link" href="/">Plan a journey <ArrowRight size={15}/></Link></article>
          <article className="quick-card dark-card"><MapPin className="card-graphic" size={86} strokeWidth={1}/><div><span className="section-kicker" style={{color:'var(--lime)'}}>Already on your way?</span><h3>Keep the journey in view.</h3><p>Look up live running status by train number and journey date. We’ll only show information from a connected provider.</p></div><Link className="mini-link" href="/runtime">Check train status <ArrowRight size={15}/></Link></article>
        </div>
        <div className="trust-strip">{[['shield','Private by design','No account needed to retrieve a ticket.'],['route','Made for Indian rail','Railway journeys, not generic travel.'],['clock','Details when you need them','Tickets and trip updates in one place.']].map(([kind,title,desc])=><div className="trust-item" key={title}><ShieldCheck size={19}/><div><strong>{title}</strong>{desc}</div></div>)}</div>
      </section>
    </main><Footer/>
  </>;
}
function Footer() { return <footer className="footer"><span>© {new Date().getFullYear()} raily. Made for the miles between.</span><div className="footer-links"><Link href="/ticket">Retrieve a ticket</Link><Link href="/runtime">Train status</Link><Link href="/sign-in">Your account</Link></div></footer>; }

function PageHeader({kicker,title,description,provider=false}) { return <div className="page-heading"><div><span className="section-kicker">{kicker}</span><h1>{title}</h1><p>{description}</p></div>{provider && <ProviderBadge/>}</div>; }
function ErrorNotice({error,retry}) { return <div className="notice error" role="alert"><WifiOff size={18}/><div><strong>We couldn’t reach railway data</strong>{errText(error)}{retry && <div style={{marginTop:10}}><button className="btn btn-small btn-quiet" onClick={retry}>Try again</button></div>}</div></div>; }

export function SearchPage() {
  const [location] = useLocation();
  const params = new URLSearchParams(location.split('?')[1] || '');
  const [fromStation,setFrom] = useState(params.get('fromStation') || '');
  const [toStation,setTo] = useState(params.get('toStation') || '');
  const [journeyDate,setDate] = useState(params.get('journeyDate') || today());
  const [submitted,setSubmitted] = useState(Boolean(params.get('fromStation') && params.get('toStation')));
  const searchParams = useMemo(()=>({fromStation,toStation,journeyDate}),[fromStation,toStation,journeyDate]);
  const query = useSearchTrains(searchParams,{query:{enabled:submitted,queryKey:getSearchTrainsQueryKey(searchParams),retry:false}});
  const submit = (e)=>{e.preventDefault();setSubmitted(true);};
  return <main className="page-main"><PageHeader kicker="Route search" title="Find your train." description="Tell us where you’re headed. We’ll check the live railway connection." provider/>
    <form className="panel" onSubmit={submit} style={{marginBottom:24}}><div className="form-grid">
      <Control label="From station"><input value={fromStation} onChange={e=>setFrom(e.target.value)} placeholder="Station or city" minLength="2" required data-testid="input-search-from"/></Control>
      <Control label="To station"><input value={toStation} onChange={e=>setTo(e.target.value)} placeholder="Station or city" minLength="2" required data-testid="input-search-to"/></Control>
      <Control label="Journey date"><input type="date" min={today()} value={journeyDate} onChange={e=>setDate(e.target.value)} required data-testid="input-search-date"/></Control>
      <div style={{display:'flex',alignItems:'end'}}><button className="btn" type="submit" disabled={query.isFetching} data-testid="button-search-submit"><Search size={15}/>{query.isFetching?'Checking…':'Search trains'}</button></div>
    </div></form>
    {!submitted ? <div className="panel status-empty"><div className="empty-icon"><TrainFront/></div><h2>Your next journey starts here</h2><p>Enter your origin, destination and travel date. Train options appear only when a live railway provider returns them.</p></div> : query.isLoading ? <div className="loading-skeleton"/> : query.isError ? <ErrorNotice error={query.error} retry={()=>query.refetch()}/> : !query.data?.length ? <div className="notice neutral"><CircleHelp size={18}/><div><strong>No live options returned</strong>There are no results from the railway provider for this route and date. Try another route or travel date.</div></div> : <div>{query.data.map(train=><TrainCard key={train.trainNumber} train={train} date={journeyDate}/>)}</div>}
  </main>;
}
function TrainCard({train,date}) {
  const link = `/book/${encodeURIComponent(train.trainNumber)}?${new URLSearchParams({fromStation:train.fromStation,toStation:train.toStation,journeyDate:date,trainName:train.trainName,departure:train.departure,arrival:train.arrival})}`;
  return <article className="train-card" data-testid={`card-train-${train.trainNumber}`}><div><div className="train-title">{train.trainName}</div><div className="train-number">{train.trainNumber}</div></div><div><div className="route-time">{train.departure}</div><div className="route-station">{stationLabel(train.fromStation)}</div></div><div><div className="route-line">{Math.floor(train.durationMinutes/60)}h {train.durationMinutes%60}m</div><div className="route-time">{train.arrival}</div><div className="route-station">{stationLabel(train.toStation)}</div><div>{train.classes?.map(c=><span className="class-chip" key={c.classCode}>{c.classCode} · {c.availableSeats} seats · {inr(c.fareINR)}</span>)}</div></div><Link className="btn btn-small" href={link}>Choose train <ArrowRight size={13}/></Link></article>;
}
function Control({label,children}) { return <div className="control"><label>{label}</label>{children}</div>; }

export function RuntimePage() {
  const [trainNumber,setTrainNumber]=useState('');
  const [journeyDate,setJourneyDate]=useState(today());
  const [request,setRequest]=useState(null);
  const stableParams = useMemo(()=>({journeyDate:request?.journeyDate || journeyDate}),[request,journeyDate]);
  const query=useGetTrainRuntimeStatus(request?.trainNumber || '',stableParams,{query:{enabled:Boolean(request),queryKey:getGetTrainRuntimeStatusQueryKey(request?.trainNumber || '',stableParams),retry:false}});
  const submit=(e)=>{e.preventDefault();if(trainNumber.trim())setRequest({trainNumber:trainNumber.trim(),journeyDate});};
  return <main className="page-main"><PageHeader kicker="On the move" title="Where’s your train?" description="Look up current running information with a train number and journey date." provider/>
    <form className="panel" onSubmit={submit} style={{maxWidth:760,marginBottom:24}}><div className="form-grid"><Control label="Train number"><input inputMode="numeric" value={trainNumber} onChange={e=>setTrainNumber(e.target.value)} placeholder="For example, your train number" minLength="2" required data-testid="input-runtime-number"/></Control><Control label="Journey date"><input type="date" value={journeyDate} onChange={e=>setJourneyDate(e.target.value)} required data-testid="input-runtime-date"/></Control><div><button className="btn" disabled={query.isFetching} data-testid="button-runtime-search"><MapPin size={15}/>{query.isFetching?'Checking live status…':'Check live status'}</button></div></div></form>
    {!request ? <div className="panel status-empty" style={{maxWidth:760}}><div className="empty-icon"><MapPin/></div><h2>Live status, when it’s connected</h2><p>Enter a train number to request its latest status. No running information is stored or estimated here.</p></div> : query.isLoading ? <div className="loading-skeleton"/> : query.isError ? <ErrorNotice error={query.error} retry={()=>query.refetch()}/> : query.data && <div className="panel" style={{maxWidth:760}}><div style={{display:'flex',justifyContent:'space-between',gap:15,alignItems:'center'}}><div><div className="section-kicker">Train {query.data.trainNumber}</div><h2 className="panel-title" style={{fontSize:22,marginTop:8}}>{query.data.isRunning?'Currently running':'Not currently running'}</h2></div><span className="provider-pill"><i className="provider-dot" style={{background:'#4eaa72'}}/>Live provider update</span></div><div className="runtime-track"><div className="runtime-stop active"><strong>{query.data.currentLocation || 'Current location unavailable'}</strong><span>Current location · Updated {query.data.updatedAt ? new Date(query.data.updatedAt).toLocaleString('en-IN') : 'time unavailable'}</span></div><div className="runtime-stop"><strong>{query.data.nextStation || 'Next station unavailable'}</strong><span>Next station</span></div><div className="runtime-stop"><strong>{query.data.expectedArrival || 'Arrival estimate unavailable'}</strong><span>Expected arrival{typeof query.data.delayMinutes==='number'?` · ${query.data.delayMinutes} min ${query.data.delayMinutes>0?'late':'delay'}`:''}</span></div></div></div>}
  </main>;
}

export function TicketPage() {
  const [pnr,setPnr]=useState('');
  const [mobileLast4,setMobile]=useState('');
  const [submitted,setSubmitted]=useState(null);
  const params=useMemo(()=>({mobileLast4:submitted?.mobileLast4 || mobileLast4}),[submitted,mobileLast4]);
  const query=useGetTicketByPnr(submitted?.pnr || '',params,{query:{enabled:Boolean(submitted),queryKey:getGetTicketByPnrQueryKey(submitted?.pnr || '',params),retry:false}});
  const submit=(e)=>{e.preventDefault();setSubmitted({pnr:pnr.trim(),mobileLast4});};
  const ticket=query.data;
  const pdfHref=ticket && `/api/tickets/${encodeURIComponent(ticket.pnr)}/pdf?mobileLast4=${encodeURIComponent(submitted.mobileLast4)}`;
  return <main className="page-main"><PageHeader kicker="No account needed" title="Your ticket, found." description="Enter the PNR and the last four digits of the booking mobile number."/>
    <form className="panel" onSubmit={submit} style={{maxWidth:680,marginBottom:24}}><p className="panel-title">Retrieve a booked ticket</p><p className="panel-sub">We use these details to verify the ticket. They are not saved to your account.</p><div className="form-grid"><Control label="PNR number"><input value={pnr} onChange={e=>setPnr(e.target.value)} inputMode="numeric" pattern="[0-9]{10}" maxLength="10" placeholder="10-digit PNR" required data-testid="input-pnr"/></Control><Control label="Mobile number · last four digits"><input value={mobileLast4} onChange={e=>setMobile(e.target.value.replace(/\D/g,'').slice(0,4))} inputMode="numeric" pattern="[0-9]{4}" maxLength="4" placeholder="••••" required data-testid="input-mobile-last4"/></Control><div><button className="btn" disabled={query.isFetching} data-testid="button-retrieve-ticket"><FileSearch size={15}/>{query.isFetching?'Verifying…':'Find ticket'}</button></div></div></form>
    {!submitted ? <div className="notice neutral" style={{maxWidth:680}}><ShieldCheck size={18}/><div><strong>Private ticket retrieval</strong>No sign-in is required. Keep your PNR and booking mobile number handy.</div></div> : query.isLoading ? <div className="loading-skeleton"/> : query.isError ? <ErrorNotice error={query.error} retry={()=>query.refetch()}/> : ticket && <div className="ticket-card" style={{maxWidth:850}} data-testid="ticket-result"><div className="ticket-head"><div><h2>{ticket.trainName}</h2><p>TRAIN {ticket.trainNumber} · {niceDate(ticket.journeyDate)}</p></div><div className="pnr-stamp">PNR<br/><strong style={{fontSize:16}}>{ticket.pnr}</strong></div></div><div className="ticket-body"><div className="ticket-route"><div><div className="route-time">{ticket.departure}</div><div className="route-station">{ticket.fromStation}</div></div><div className="ticket-midline"><ArrowRight size={17}/><br/>{ticket.classCode}</div><div><div className="route-time">{ticket.arrival}</div><div className="route-station">{ticket.toStation}</div></div></div><div className="ticket-divider"/><div className="ticket-meta"><div><label>Date</label><strong>{niceDate(ticket.journeyDate)}</strong></div><div><label>Class</label><strong>{ticket.classCode}</strong></div><div><label>Booking status</label><strong>{ticket.bookingStatus}</strong></div><div><label>Fare</label><strong>{inr(ticket.fareINR)}</strong></div></div>{ticket.passengers?.map((p,i)=><div className="passenger-row" key={`${p.name}-${i}`}><span><strong>{i+1}. {p.name}</strong> · {p.age} yrs · {p.gender}</span><span>{p.seatNumber || 'Seat details pending'}</span></div>)}<div style={{display:'flex',justifyContent:'space-between',alignItems:'center',gap:15,marginTop:22}}><span className="muted-text" style={{fontSize:10}}>Payment · {ticket.paymentStatus}</span><a className="btn btn-small" href={pdfHref} download={`ticket-${ticket.pnr}.pdf`} data-testid="link-ticket-pdf"><Download size={14}/>Download PDF</a></div></div></div>}
  </main>;
}

export function SignInPage() {
  const [,setLocation]=useLocation(); const client=useQueryClient(); const login=useLogIn();
  const [form,setForm]=useState({email:'',password:''}); const [error,setError]=useState('');
  const submit=(e)=>{e.preventDefault();setError('');login.mutate({data:form},{onSuccess:()=>{client.invalidateQueries({queryKey:getGetSessionQueryKey()});setLocation('/account');},onError:(e)=>setError(errText(e))});};
  return <AuthLayout><form className="auth-form" onSubmit={submit}><span className="section-kicker">Welcome back</span><h1>Sign in to raily.</h1><p>Your trips and travel details, together in one place.</p><Control label="Email address"><input type="email" autoComplete="email" value={form.email} onChange={e=>setForm({...form,email:e.target.value})} required data-testid="input-signin-email"/></Control><Control label="Password"><input type="password" autoComplete="current-password" minLength="8" value={form.password} onChange={e=>setForm({...form,password:e.target.value})} required data-testid="input-signin-password"/></Control>{error&&<p className="error-text" role="alert">{error}</p>}<button className="btn" style={{width:'100%',marginTop:8}} disabled={login.isPending} data-testid="button-signin">{login.isPending?'Signing in…':'Sign in'}<ArrowRight size={15}/></button><div className="auth-switch">New to raily? <Link href="/sign-up">Create an account</Link></div><div className="auth-switch">Only need a ticket? <Link href="/ticket">Retrieve it without signing in</Link></div></form></AuthLayout>;
}
export function SignUpPage() {
  const [,setLocation]=useLocation(); const client=useQueryClient(); const signup=useSignUp();
  const [form,setForm]=useState({name:'',email:'',phone:'',password:''}); const [error,setError]=useState('');
  const submit=(e)=>{e.preventDefault();setError('');signup.mutate({data:form},{onSuccess:()=>{client.invalidateQueries({queryKey:getGetSessionQueryKey()});setLocation('/account');},onError:(e)=>setError(errText(e))});};
  return <AuthLayout><form className="auth-form" onSubmit={submit}><span className="section-kicker">A little more organised</span><h1>Make room for the journey.</h1><p>Create an account for a handy place to manage your bookings.</p><Control label="Full name"><input autoComplete="name" minLength="2" value={form.name} onChange={e=>setForm({...form,name:e.target.value})} required data-testid="input-signup-name"/></Control><Control label="Email address"><input type="email" autoComplete="email" value={form.email} onChange={e=>setForm({...form,email:e.target.value})} required data-testid="input-signup-email"/></Control><Control label="Mobile number"><input type="tel" autoComplete="tel" minLength="10" maxLength="16" value={form.phone} onChange={e=>setForm({...form,phone:e.target.value})} required data-testid="input-signup-phone"/></Control><Control label="Password"><input type="password" autoComplete="new-password" minLength="8" value={form.password} onChange={e=>setForm({...form,password:e.target.value})} required data-testid="input-signup-password"/></Control>{error&&<p className="error-text" role="alert">{error}</p>}<button className="btn" style={{width:'100%',marginTop:6}} disabled={signup.isPending} data-testid="button-signup">{signup.isPending?'Creating account…':'Create account'}<ArrowRight size={15}/></button><div className="auth-switch">Already have an account? <Link href="/sign-in">Sign in</Link></div></form></AuthLayout>;
}
function AuthLayout({children}) { return <div className="auth-layout"><div className="auth-art"><span className="eyebrow">A steadier travel day</span><h2>Good journeys<br/>start with clarity.</h2><p>Keep your railway bookings close, your ticket easy to find, and the next step clear.</p></div><div className="auth-form-wrap">{children}</div></div>; }

export function AccountPage() {
  const client=useQueryClient(); const session=useGetSession(); const authenticated=Boolean(session.data?.authenticated);
  const profile=useGetMyProfile({query:{enabled:authenticated,queryKey:getGetMyProfileQueryKey(),retry:false}});
  const summary=useGetMyBookingSummary({query:{enabled:authenticated,queryKey:getGetMyBookingSummaryQueryKey(),retry:false}});
  const bookings=useListMyBookings({query:{enabled:authenticated,queryKey:getListMyBookingsQueryKey(),retry:false}});
  const update=useUpdateMyProfile(); const logout=useLogOut();
  const [editing,setEditing]=useState(false); const [form,setForm]=useState({name:'',phone:''}); const [notice,setNotice]=useState('');
  const startEdit=()=>{setForm({name:profile.data?.name||'',phone:profile.data?.phone||''});setEditing(true);setNotice('');};
  const save=(e)=>{e.preventDefault();update.mutate({data:form},{onSuccess:()=>{client.invalidateQueries({queryKey:getGetMyProfileQueryKey()});setEditing(false);setNotice('Profile updated.');},onError:e=>setNotice(errText(e))});};
  const signout=()=>logout.mutate(undefined,{onSuccess:()=>{client.invalidateQueries({queryKey:getGetSessionQueryKey()});setNotice('You have signed out.');},onError:e=>setNotice(errText(e))});
  if(session.isLoading)return <main className="page-main"><div className="loading-skeleton"/></main>;
  if(!authenticated)return <main className="page-main"><PageHeader kicker="Your space" title="A place for your journeys." description="Sign in to see your profile, booking history and travel summary."/><div className="panel status-empty"><div className="empty-icon"><UserRound/></div><h2>Sign in to your account</h2><p>Ticket retrieval is always available without an account. For booking history and profile details, sign in.</p><div style={{display:'flex',justifyContent:'center',gap:10,marginTop:18}}><Link className="btn" href="/sign-in">Sign in</Link><Link className="btn btn-quiet" href="/ticket">Retrieve ticket</Link></div></div></main>;
  return <main className="page-main"><PageHeader kicker="Your space" title="Welcome back." description="A clear view of what’s coming up and where you’ve been."/><div className="account-grid"><aside className="panel profile-card"><div className="avatar">{(profile.data?.name||'R').slice(0,1).toUpperCase()}</div><div className="profile-name">{profile.data?.name||'Your profile'}</div><div className="profile-email">{profile.data?.email}</div><div className="booking-detail" style={{marginTop:8}}>{profile.data?.phone}</div>{notice&&<div className="error-text" role="status">{notice}</div>}<div style={{display:'flex',gap:8,marginTop:21}}><button className="btn btn-small btn-quiet" onClick={startEdit} data-testid="button-edit-profile">Edit profile</button><button className="btn btn-small btn-dark" onClick={signout} disabled={logout.isPending} data-testid="button-signout">{logout.isPending?'Signing out…':'Sign out'}</button></div></aside><div><div className="account-stats"><div className="stat-card"><strong>{summary.data?.upcoming ?? '—'}</strong><span>Upcoming</span></div><div className="stat-card"><strong>{summary.data?.previous ?? '—'}</strong><span>Previous</span></div><div className="stat-card"><strong>{summary.data?.total ?? '—'}</strong><span>Total journeys</span></div></div><section className="panel"><h2 className="panel-title">Your bookings</h2><p className="panel-sub">A quick look at your railway journeys.</p>{bookings.isLoading?<div className="loading-skeleton"/>:bookings.isError?<ErrorNotice error={bookings.error} retry={()=>bookings.refetch()}/>:!bookings.data?.length?<div className="status-empty"><div className="empty-icon"><TrainFront/></div><h2>No bookings just yet</h2><p>Your bookings will be listed here after you book a train.</p><Link className="btn btn-small" style={{marginTop:16}} href="/">Plan a journey</Link></div>:bookings.data.map(item=><div className="booking-row" key={item.id}><div><div className="booking-route">{stationLabel(item.fromStation)} <ArrowRight size={12}/> {stationLabel(item.toStation)}</div><div className="booking-detail">{item.trainName} · {niceDate(item.journeyDate)} · {item.classCode}{item.pnr?` · PNR ${item.pnr}`:''}</div></div><span className={`booking-status ${String(item.status).toLowerCase()}`}>{String(item.status).replaceAll('_',' ')}</span></div>)}</section></div></div>{editing&&<div className="modal-backdrop" role="presentation"><form className="panel edit-modal" onSubmit={save}><button type="button" className="modal-close" onClick={()=>setEditing(false)} aria-label="Close"><X/></button><h2 className="panel-title">Update your details</h2><p className="panel-sub">Keep your account information up to date.</p><Control label="Full name"><input value={form.name} minLength="2" required onChange={e=>setForm({...form,name:e.target.value})} data-testid="input-profile-name"/></Control><div style={{height:15}}/><Control label="Mobile number"><input value={form.phone} minLength="10" maxLength="16" required onChange={e=>setForm({...form,phone:e.target.value})} data-testid="input-profile-phone"/></Control><button className="btn" style={{marginTop:18,width:'100%'}} disabled={update.isPending} data-testid="button-save-profile">{update.isPending?'Saving…':'Save changes'}</button></form></div>}</main>;
}

export function BookingPage() {
  const params=useParams(); const trainNumber=params.trainNumber || '';
  const [location,setLocation]=useLocation(); const client=useQueryClient();
  const search=new URLSearchParams(location.split('?')[1]||'');
  const fromStation=search.get('fromStation')||''; const toStation=search.get('toStation')||''; const journeyDate=search.get('journeyDate')||'';
  const queryParams=useMemo(()=>({fromStation,toStation,journeyDate}),[fromStation,toStation,journeyDate]);
  const canVerify=Boolean(trainNumber&&fromStation&&toStation&&journeyDate);
  const trains=useSearchTrains(queryParams,{query:{enabled:canVerify,queryKey:getSearchTrainsQueryKey(queryParams),retry:false}});
  const train=trains.data?.find(item=>item.trainNumber===trainNumber);
  const [selectedClass,setSelectedClass]=useState('');
  const [passengers,setPassengers]=useState([{name:'',age:'',gender:'',berthPreference:'NO_PREFERENCE'}]);
  const [booking,setBooking]=useState(null);
  const [paymentResult,setPaymentResult]=useState(null);
  const [error,setError]=useState('');
  const create=useCreateBooking(); const pay=usePayForBooking();
  const activeClass=train?.classes?.find(item=>item.classCode===selectedClass) || train?.classes?.[0];
  const fare=Number(activeClass?.fareINR||0)*passengers.length;
  const submit=(e)=>{e.preventDefault();setError('');if(!train)return;const classCode=selectedClass||train.classes?.[0]?.classCode;if(!classCode)return setError('No live class options were returned for this train.');create.mutate({data:{trainNumber:train.trainNumber,classCode,journeyDate,passengers:passengers.map(p=>({name:p.name.trim(),age:Number(p.age),gender:p.gender,berthPreference:p.berthPreference||null}))}},{onSuccess:(result)=>{setBooking(result);client.invalidateQueries({queryKey:getListMyBookingsQueryKey()});client.invalidateQueries({queryKey:getGetMyBookingSummaryQueryKey()});},onError:e=>setError(errText(e))});};
  const doPayment=(decision)=>{setError('');pay.mutate({bookingId:booking.id,data:{decision}},{onSuccess:result=>{setPaymentResult(result);client.invalidateQueries({queryKey:getListMyBookingsQueryKey()});client.invalidateQueries({queryKey:getGetMyBookingSummaryQueryKey()});},onError:e=>setError(errText(e))});};
  const patchPassenger=(index,key,value)=>setPassengers(passengers.map((p,i)=>i===index?{...p,[key]:value}:p));
  return <main className="page-main"><PageHeader kicker="Passenger details" title="Let’s get you on board." description="Confirm the live train, add passenger details and complete a sandbox payment." provider/>
    {!canVerify ? <div className="notice neutral"><CircleHelp size={18}/><div><strong>Train details need to be verified</strong>Start with a live search and choose a train from the returned results. We can’t book a train from a manually entered number.<div style={{marginTop:12}}><Link className="btn btn-small" href="/">Search live trains</Link></div></div></div> : trains.isLoading ? <div className="loading-skeleton"/> : trains.isError ? <ErrorNotice error={trains.error} retry={()=>trains.refetch()}/> : !train ? <div className="notice neutral"><TrainFront size={18}/><div><strong>This train isn’t available to book</strong>The live railway provider did not return train {trainNumber} for this route and date. Please search again for current options.<div style={{marginTop:12}}><Link className="btn btn-small" href="/">Search again</Link></div></div></div> : <>
    <div className="booking-columns"><form className="panel" onSubmit={submit}><h2 className="panel-title">Who’s travelling?</h2><p className="panel-sub">Passenger details should match the traveller’s identification.</p>{passengers.map((p,i)=><div className="passenger-form" key={i}><div className="passenger-form-head"><span>Passenger {i+1}</span>{passengers.length>1&&<button type="button" onClick={()=>setPassengers(passengers.filter((_,index)=>index!==i))} aria-label={`Remove passenger ${i+1}`} style={{background:'none',border:0,color:'#9b5145',cursor:'pointer'}}>Remove</button>}</div><div className="inline-fields"><Control label="Full name"><input value={p.name} onChange={e=>patchPassenger(i,'name',e.target.value)} minLength="2" maxLength="100" required data-testid={`input-passenger-name-${i}`}/></Control><Control label="Age"><input type="number" value={p.age} onChange={e=>patchPassenger(i,'age',e.target.value)} min="1" max="120" required data-testid={`input-passenger-age-${i}`}/></Control><Control label="Gender"><select value={p.gender} onChange={e=>patchPassenger(i,'gender',e.target.value)} required data-testid={`select-passenger-gender-${i}`}><option value="">Choose</option><option value="MALE">Male</option><option value="FEMALE">Female</option><option value="OTHER">Other</option></select></Control><Control label="Berth preference"><select value={p.berthPreference} onChange={e=>patchPassenger(i,'berthPreference',e.target.value)}><option value="NO_PREFERENCE">No preference</option><option value="LOWER">Lower</option><option value="MIDDLE">Middle</option><option value="UPPER">Upper</option><option value="SIDE_LOWER">Side lower</option><option value="SIDE_UPPER">Side upper</option></select></Control></div></div>)}{passengers.length<6&&<button type="button" className="add-passenger" onClick={()=>setPassengers([...passengers,{name:'',age:'',gender:'',berthPreference:'NO_PREFERENCE'}])} data-testid="button-add-passenger">+ Add another passenger</button>}<Control label="Travel class"><select value={selectedClass||train.classes?.[0]?.classCode||''} onChange={e=>setSelectedClass(e.target.value)} required data-testid="select-travel-class">{train.classes?.map(c=><option key={c.classCode} value={c.classCode}>{c.classCode} · {c.availableSeats} live seats · {inr(c.fareINR)}</option>)}</select></Control>{error&&<p className="error-text" role="alert">{error}</p>}{!booking&&<button type="submit" className="btn" style={{marginTop:18,width:'100%'}} disabled={create.isPending||!train.classes?.length} data-testid="button-create-booking">{create.isPending?'Creating booking…':'Continue to sandbox payment'}<ArrowRight size={15}/></button>}</form>
    <aside><div className="panel"><span className="section-kicker">Your journey</span><h2 className="panel-title" style={{fontSize:18,marginTop:10}}>{train.trainName}</h2><p className="panel-sub">Train {train.trainNumber} · {niceDate(journeyDate)}</p><div className="ticket-route"><div><div className="route-time">{train.departure}</div><div className="route-station">{stationLabel(train.fromStation)}</div></div><div className="ticket-midline"><ArrowRight size={16}/><br/>{Math.floor(train.durationMinutes/60)}h {train.durationMinutes%60}m</div><div><div className="route-time">{train.arrival}</div><div className="route-station">{stationLabel(train.toStation)}</div></div></div><div className="ticket-divider"/><div style={{display:'flex',justifyContent:'space-between',fontSize:12}}><span>Class · {selectedClass||train.classes?.[0]?.classCode}</span><strong>{inr(fare)}</strong></div><div style={{display:'flex',justifyContent:'space-between',fontSize:11,color:'var(--muted)',marginTop:10}}><span>{passengers.length} traveller{passengers.length===1?'':'s'}</span><span>Fare from live provider</span></div></div>{booking&&paymentResult?.status!=='SUCCESS'&&<div className="panel" style={{marginTop:16}}><h2 className="panel-title">Sandbox payment</h2><p className="panel-sub">This is a test payment flow. No card or UPI credentials are collected.</p><div className="notice neutral"><ShieldCheck size={17}/><div><strong>Booking {booking.id}</strong>Choose an outcome to test booking confirmation.</div></div><button className="btn" style={{width:'100%',marginTop:14}} onClick={()=>doPayment('APPROVE')} disabled={pay.isPending} data-testid="button-sandbox-approve">{pay.isPending?'Processing…':'Approve test payment'}</button><button className="btn btn-quiet" style={{width:'100%',marginTop:9}} onClick={()=>doPayment('DECLINE')} disabled={pay.isPending} data-testid="button-sandbox-decline">Decline test payment</button></div>}{paymentResult&&<div className={`notice ${paymentResult.status==='SUCCESS'?'success':'error'}`} style={{marginTop:16}}><Check size={18}/><div><strong>{paymentResult.status==='SUCCESS'?'Booking confirmed':'Payment was not completed'}</strong>{paymentResult.status==='SUCCESS'&&paymentResult.pnr?<>Your PNR is <b>{paymentResult.pnr}</b>. <Link href="/ticket">Retrieve your ticket</Link>.</>:'You can try another sandbox outcome from your booking.'}</div></div>}</aside></div></>}
  </main>;
}
export function NotFoundPage() { return <main className="page-main"><div className="panel status-empty"><div className="empty-icon"><TrainFront/></div><h2>This stop isn’t on the map.</h2><p>That page may have moved. Head back and plan your journey.</p><Link className="btn" style={{marginTop:18}} href="/">Back to home</Link></div></main>; }
