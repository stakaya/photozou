#!c:/Perl/bin/perl
#!/usr/bin/perl

use strict;
use warnings;
use LWP::UserAgent;
use HTTP::Request;
use HTTP::Request::Common;
use URI;
use XML::Simple;
use Encode;
use utf8;

print("Content-type: text/html\n\n");
my $date;
my $sim;
my $term;
my $seq;
my $type;
my $length;
my $bar;
my $mail;
my $password;
my $album;
my $data;
my $file;

binmode(STDIN);
read(STDIN, $date,     14);
read(STDIN, $sim,      20);
read(STDIN, $term,     15);
read(STDIN, $seq,       5);
read(STDIN, $type,      5);
read(STDIN, $mail,    256);
read(STDIN, $password, 20);
read(STDIN, $album,    20);
read(STDIN, $length,    6);

$date     = trim($date);
$sim      = trim($sim);
$term     = trim($term);
$seq      = trim($seq);
$type     = trim($type);
$mail     = trim($mail);
$password = trim($password);
$album    = trim($album);
$length   = trim($length);
$file     = $date . $sim . $term;

read(STDIN, $data, int($length));

if ($seq ne 'END') {
    open(OUT, ">>$file") or die('file not found.');
    binmode OUT;
    print OUT $data;
    close(OUT);
    print 'OK';
    exit();
}

my $photo;
my $gif = '^\x47\x49\x46';
my $jpg = '^\xff\xd8\xff';

open IN, "<$file" or die 'file not found.';
binmode IN;
read(IN, $photo, 10);
close IN;

if ($photo =~ /$gif/) {
    rename $file, $file . '.gif';
    $file = $file . '.gif';
} elsif ($photo =~ /$jpg/) {
    rename $file, $file . '.jpg';
    $file = $file . '.jpg';
} else {
    rename $file, $file . '.3gp';
    $file = $file . '.3gp';
}

my $apiurl = 'http://api.photozou.jp/rest/photo_add';
my $uri = URI->new($apiurl);
my $host = $uri->host;
my $port = $uri->port;
my $netloc = sprintf "%s:%d", $host, $port;
my $realm = "photo";
my $ua = LWP::UserAgent->new;

# basic認証
$ua->credentials($netloc, $realm, $mail ,$password);

# 文字コード変換
$data  = Encode::encode('utf8', Encode::decode('shiftjis', $data));
$album = Encode::encode('utf8', Encode::decode('shiftjis', $album));
$date  = Encode::encode('utf8', Encode::decode('shiftjis', $date));

if ($data eq '') {
    $data = 'Powered by monysong.com';
}

my $req = POST($apiurl,
               Content_Type => 'form-data',
               Content => [
                 photo       => [$file],
                 album_id    => $album,
                 photo_title => $data,
                 tag         => 'monysong.com',
                 description => 'Powered by monysong.com',
                 date_type   => 'date',
                 year        => substr($date, 0, 4),
                 month       => substr($date, 4, 2),
                 day         => substr($date, 6, 2)]);
# ファイル送信
my $res = $ua->request($req);

# 結果判定
if ($res->is_success) {
    my $result = XML::Simple::XMLin($res->content);
    if ($result->{stat} eq 'fail') {
        print "NG\n";
        print $result->{err};
    } else {
        unlink($file);
        print "OK\n";
    }
} else {
    print "NG\n";
    print $res->status_line;
}

# トリム関数
sub trim {
    my $val = shift;
    $val =~ s/^ *(.*?) *$/$1/;
    return $val;
}
