/** Rendered while auth status is `checking`, so protected content never flashes. */
export function LoadingPage() {
  return <p role="status">Checking your session…</p>
}
