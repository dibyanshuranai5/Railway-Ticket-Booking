import { type ReactNode } from 'react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { ErrorBoundary } from '@/components/error-boundary';
import { Toaster } from '@/components/ui/toaster';
import { TooltipProvider } from '@/components/ui/tooltip';
import { Route, Switch, useLocation, Router as WouterRouter } from 'wouter';
import {
  HomePage, SearchPage, RuntimePage, TicketPage, SignInPage, SignUpPage,
  AccountPage, BookingPage, NotFoundPage, Shell,
} from './pages/Pages.jsx';
import './index.css';

const queryClient = new QueryClient();
function Router() {
  return <RoutedErrorBoundary><Shell><Switch>
    <Route path="/" component={HomePage} />
    <Route path="/search" component={SearchPage} />
    <Route path="/runtime" component={RuntimePage} />
    <Route path="/ticket" component={TicketPage} />
    <Route path="/sign-in" component={SignInPage} />
    <Route path="/sign-up" component={SignUpPage} />
    <Route path="/account" component={AccountPage} />
    <Route path="/book/:trainNumber" component={BookingPage} />
    <Route component={NotFoundPage} />
  </Switch></Shell></RoutedErrorBoundary>;
}
function RoutedErrorBoundary({ children }: { children: ReactNode }) {
  const [location] = useLocation();
  return <ErrorBoundary resetKey={location}>{children}</ErrorBoundary>;
}
function App() {
  return <QueryClientProvider client={queryClient}><TooltipProvider>
    <WouterRouter base={import.meta.env.BASE_URL.replace(/\/$/, '')}><Router /></WouterRouter>
    <Toaster />
  </TooltipProvider></QueryClientProvider>;
}
export default App;
